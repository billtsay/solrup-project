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
import org.apache.commons.scxml2.model.*;
import org.apache.log4j.Logger;
import org.finra.datagenerator.distributor.ProcessingStrategy;
import org.finra.datagenerator.engine.Frontier;
import org.finra.datagenerator.engine.scxml.tags.CustomTagExtension;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Frontier implementation for generating data with SCXML state machine models.
 */
public class SCXMLFrontier extends SCXMLExecutor implements Frontier {

    private final PossibleState root;
    private static final Logger log = Logger.getLogger(SCXMLFrontier.class);
    private List<CustomTagExtension> tagExtensionList;

    /**
     * Constructor
     *
     * @param possibleState the root node of the model and partial variable assignment to start a dfs from
     * @param model the model text
     * @param tagExtensionList custom tags used in this model
     */
    public SCXMLFrontier(final PossibleState possibleState, final SCXML model,
                         final List<CustomTagExtension> tagExtensionList) {
        root = possibleState;
        this.tagExtensionList = tagExtensionList;
        try {
            this.setStateMachine(model);
        } catch (ModelException e) {
            e.printStackTrace();
        }

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
     * Constructor
     *
     * @param possibleState the root node of the model and partial variable assignment to start a dfs from
     * @param model the model text
     */
    public SCXMLFrontier(final PossibleState possibleState, final SCXML model) {
        this(possibleState, model, new LinkedList<CustomTagExtension>());
    }

    /**
     * Performs a DFS on the model, starting from root, giving results to the processingStrategy
     * Just a public wrapper for private dfs function
     *
     * @param processingStrategy the results handler
     * @param flag used to stop the search before completion
     */
    public void searchForScenarios(ProcessingStrategy processingStrategy, AtomicBoolean flag) {
        dfs(processingStrategy, flag, root);
    }

    private void dfs(ProcessingStrategy processingStrategy, AtomicBoolean flag, PossibleState state) {
        if (flag.get()) {
            return;
        }

        TransitionTarget nextState = state.nextState;

        //reached end of chart, valid assignment found
        if (nextState.getId().equalsIgnoreCase("end")) {
            processingStrategy.processOutput(state.variables);

            return;
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

            //go through every transition and see which of the products are valid, recursive searching on them
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

                    //transition condition satisfied, continue search recursively
                    if (pass) {
                        Set<TransitionTarget> targets = transition.getTargets();
                        if (targets.size()>0) {
                            PossibleState result = new PossibleState(targets.iterator().next(), p);
                            dfs(processingStrategy, flag, result);
                        }
                    }
                }
            }
        }
    }

    public PossibleState getRoot() {
        return root;
    }
}