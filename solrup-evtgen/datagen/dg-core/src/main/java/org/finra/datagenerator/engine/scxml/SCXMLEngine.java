/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.engine.scxml;

import org.apache.commons.scxml2.Context;
import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.SCXMLExpressionException;
import org.apache.commons.scxml2.env.jexl.JexlContext;
import org.apache.commons.scxml2.env.jexl.JexlEvaluator;
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.io.SCXMLReader.Configuration;
import org.apache.commons.scxml2.model.*;
import org.finra.datagenerator.distributor.SearchDistributor;
import org.finra.datagenerator.engine.Engine;
import org.finra.datagenerator.engine.Frontier;
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;
import org.finra.datagenerator.engine.scxml.tags.SetAssignExtension;
import org.finra.datagenerator.engine.scxml.tags.FileExtension;
import org.finra.datagenerator.engine.scxml.tags.RangeExtension;
import org.finra.datagenerator.engine.scxml.tags.SingleValueAssignExtension;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Engine implementation for generating data with SCXML state machine models.
 */
public class SCXMLEngine extends SCXMLExecutor implements Engine {
    private SCXML model;
    private int bootStrapMin;
    private List<CustomTagExtension> tagExtensionList;

    /**
     * Constructor
     */
    public SCXMLEngine() {
        super();

        tagExtensionList = new LinkedList<>();
        tagExtensionList.add(new SetAssignExtension());
        tagExtensionList.add(new SingleValueAssignExtension());
        tagExtensionList.add(new FileExtension());
        tagExtensionList.add(new RangeExtension());

        JexlEvaluator elEvaluator = new JexlEvaluator();
        JexlContext context = new JexlContext();

        try {
            this.setEvaluator(elEvaluator);
        } catch (ModelException e) {
            e.printStackTrace();
        }
        this.setRootContext(context);
    }

    /**
     * Alternative Constructor to support InLineTransformers within the model
     *
     * @param tagExtensionList the list of extensions to add
     */
    public SCXMLEngine(final List<CustomTagExtension> tagExtensionList) {
        this();
        // Adding all CustomTagExtensions - They will be added FIFO order
        // (Whatever CustomTagExtension that was added to the list first will be added to the engine first)
        for (CustomTagExtension cte:tagExtensionList) {
            addTagExtension(cte);
        }
    }

    /**
     * Alternative Constructor to support InLineTransformers within the model
     *
     * @param tagExtensionList the list of extensions to add
     */
    public SCXMLEngine(final CustomTagExtension... tagExtensionList) {
        this();
        // Adding all CustomTagExtensions - They will be added FIFO order
        // (Whatever CustomTagExtension that was added to the list first will be added to the engine first)
        for (CustomTagExtension cte:tagExtensionList) {
            addTagExtension(cte);
        }
    }

    /**
     * Searches the model for all variable assignments and makes a default map of those variables, setting them to ""
     *
     * @return the default variable assignment map
     */
    private Map<String, String> fillInitialVariables() {
        List<EnterableState> targets = model.getChildren();

        Set<String> variables = new HashSet<>();
        for (EnterableState state: targets){
            for (OnEntry entry: state.getOnEntries()){
                List<Action> actions = entry.getActions();
                for (Action action : actions) {
                    if (action instanceof Assign) {
                        String variable = ((Assign) action).getName();
                        variables.add(variable);
                    } else if (action instanceof SetAssignExtension.SetAssignTag) {
                        String variable = ((SetAssignExtension.SetAssignTag) action).getName();
                        variables.add(variable);
                    }
                }
            }
        }

        Map<String, String> result = new HashMap<>();
        for (String variable : variables) {
            result.put(variable, "");
        }

        return result;
    }

    /**
     * Performs a partial BFS on model until the search frontier reaches the desired bootstrap size
     *
     * @param min the desired bootstrap size
     * @return a list of found PossibleState
     * @throws ModelException if the desired bootstrap can not be reached
     */
    public List<PossibleState> bfs(int min) throws ModelException {
        List<PossibleState> bootStrap = new LinkedList<>();

        TransitionTarget initial = model.getTargets().get(model.getInitial());

        PossibleState initialState = new PossibleState(initial, fillInitialVariables());
        bootStrap.add(initialState);

        while (bootStrap.size() > 0 && bootStrap.size() < min) {
            PossibleState state = bootStrap.remove(0);
            TransitionTarget nextState = state.nextState;

            if (nextState.getId().equalsIgnoreCase("end")) {
                throw new ModelException("Could not achieve required bootstrap without reaching end state");
            }

            //run every action in series
            List<Map<String, String>> product = new LinkedList<>();
            product.add(new HashMap<>(state.variables));
            if (nextState instanceof State) {
                for (OnEntry entry : ((State) nextState).getOnEntries()) {

                    List<Action> actions = entry.getActions();

                    for (Action action : actions) {
                        for (CustomTagExtension tagExtension : tagExtensionList) {
                            if (tagExtension.getTagActionClass().isInstance(action)) {
                                product = tagExtension.pipelinePossibleStates(action, product);
                            }
                        }
                    }
                }

                //go through every transition and see which of the products are valid, adding them to the list
                List<Transition> transitions = ((State) nextState).getTransitionsList();
                for (Transition transition : transitions) {
                    String condition = transition.getCond();

                    for (Map<String, String> p : product) {
                        Boolean pass;

                        if (condition == null) {
                            pass = true;
                        } else {
                            //scrub the context clean so we may use it to evaluate transition conditional
                            Context context = this.getRootContext();
                            context.reset();

                            //set up new context
                            for (Map.Entry<String, String> e : p.entrySet()) {
                                context.set(e.getKey(), e.getValue());
                            }

                            //evaluate condition
                            try {
                                pass = (Boolean) this.getEvaluator().eval(context, condition);
                            } catch (SCXMLExpressionException ex) {
                                pass = false;
                            }
                        }

                        //transition condition satisfied, add to bootstrap list
                        if (pass) {
                            Set<TransitionTarget> targets = transition.getTargets();
                            if (targets.size()>0) {
                                PossibleState result = new PossibleState(targets.iterator().next(), p);
                                bootStrap.add(result);
                            }
                        }
                    }
                }
            }
        }

        return bootStrap;
    }

    /**
     * Performs the BFS and gives the results to a distributor to distribute
     *
     * @param distributor the distributor
     */
    public void process(SearchDistributor distributor) {
        List<PossibleState> bootStrap;
        try {
            bootStrap = bfs(bootStrapMin);
        } catch (ModelException e) {
            bootStrap = new LinkedList<>();
        }

        List<Frontier> frontiers = new LinkedList<>();
        for (PossibleState p : bootStrap) {
            SCXMLFrontier dge = new SCXMLFrontier(p, model, tagExtensionList);
            frontiers.add(dge);
        }

        distributor.distribute(frontiers);
    }

    private List<CustomAction> customActionsFromTagExtensions() {
        List<CustomAction> customActions = new ArrayList<>();

        for (CustomTagExtension tagExtension : tagExtensionList) {
            if (!tagExtension.getTagNameSpace().equals("http://www.w3.org/2005/07/scxml")) {
                CustomAction action = new CustomAction(tagExtension.getTagNameSpace(), tagExtension.getTagName(),
                        tagExtension.getTagActionClass());
                customActions.add(action);
            }
        }

        return customActions;
    }

    /**
     * Sets the SCXML model with an InputStream
     *
     * @param inputFileStream the model input stream
     */
    public void setModelByInputFileStream(InputStream inputFileStream) {
        try {
            this.model = SCXMLReader.read(inputFileStream,
                        new Configuration(null, null, customActionsFromTagExtensions()));

            this.setStateMachine(this.model);
        } catch (IOException | ModelException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the SCXML model with a string
     *
     * @param model the model text
     */
    public void setModelByText(String model) {
        try {
            InputStream is = new ByteArrayInputStream(model.getBytes());
            this.model = SCXMLReader.read(is, new Configuration(null, null, customActionsFromTagExtensions()));
            this.setStateMachine(this.model);
        } catch (IOException | ModelException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * bootstrapMin setter
     *
     * @param min sets the desired bootstrap min
     * @return this
     */
    public Engine setBootstrapMin(int min) {
        bootStrapMin = min;
        return this;
    }

    /**
     * Adds a custom tag extension to this engine for use in model parsing and processing. Custom tags should be added
     * before the model is set.
     *
     * @param tagExtension the extension to add
     */
    private void addTagExtension(CustomTagExtension tagExtension) {
        this.tagExtensionList.add(tagExtension);
    }
}
