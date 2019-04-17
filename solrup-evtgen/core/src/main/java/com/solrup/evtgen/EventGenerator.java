/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.solrup.evtgen;

import com.solrup.evtgen.annotation.State;
import com.solrup.evtgen.annotation.StateMachine;
import com.solrup.evtgen.engine.tags.FileExtension;
import com.solrup.evtgen.engine.tags.InLineTransformerExtension;
import com.solrup.evtgen.engine.tags.RangeExtension;
import com.solrup.evtgen.engine.tags.SetAssignExtension;
import org.apache.avro.generic.GenericData;
import org.apache.commons.scxml2.SCXMLListener;
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.*;

import java.io.Reader;
import java.util.*;

/**
 * A SCXML document driven stop watch.
 *
 * Using SCXML makes the StopWatch class simplistic; you are neither
 * managing the stopwatch "lifecycle" nor coding any "transitions",
 * that information is pulled in straight from the behavioral model
 * of the stop watch, which is encapsulated in the SCXML document
 * the constructor points to (which in turn may be generated straight
 * from the UML model).
 */

@StateMachine(name="evtgen", resource="classpath:evtgen.xml")
public class EventGenerator extends AbstractStateMachine {

    /** The events for the stop watch. */
    public static final String  EVENT_START     = "evtgen.start",
                                EVENT_STOP      = "evtgen.stop",
                                EVENT_SPLIT     = "evtgen.split",
                                EVENT_UNSPLIT   = "evtgen.unsplit",
                                EVENT_RESET     = "evtgen.reset";

    /** The fragments of the elapsed time. */
    private int hr, min, sec, fract;
    /** The fragments of the display time. */
    private int dhr, dmin, dsec, dfract;
    /** The stopwatch "split" (display freeze). */
    private boolean split;
    /** The Timer to keep time. */
    private Timer timer;
    /** The display decorations. */
    private static final String DELIM = ":", DOT = ".", EMPTY = "", ZERO = "0";

    public EventGenerator() throws ModelException {
        super(EventGenerator.class.getClassLoader().getResource("evtgen.xml"), null);
    }

    // Each method below is the activity corresponding to a state in the
    // SCXML document (see class constructor for pointer to the document).
    public void reset() {
        hr = min = sec = fract = dhr = dmin = dsec = dfract = 0;
        split = false;
    }

    @State(id = "running")
    public void running() {
        split = false;
        if (timer == null) {
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    increment();
                }
            }, 100, 100);
        }
    }

    public void paused() {
        split = true;
    }

    public void stopped() {
        timer.cancel();
        timer = null;
    }

    public String getDisplay() {
        String padhr = dhr > 9 ? EMPTY : ZERO;
        String padmin = dmin > 9 ? EMPTY : ZERO;
        String padsec = dsec > 9 ? EMPTY : ZERO;
        return new StringBuffer().append(padhr).append(dhr).append(DELIM).
            append(padmin).append(dmin).append(DELIM).append(padsec).
            append(dsec).append(DOT).append(dfract).toString();
    }

    // used by the demonstration (see StopWatchDisplay usecase)
    public String getCurrentState() {
        return getEngine().getCurrentStatus().getStates().iterator().next().getId();
    }

    private void increment() {
        if (fract < 9) {
            fract++;
        } else {
            fract = 0;
            if (sec < 59) {
                sec++;
            } else {
                sec = 0;
                if (min < 59) {
                    min++;
                } else {
                    min = 0;
                    if (hr < 99) {
                        hr++;
                    } else {
                        hr = 0; //wrap
                    }
                }
            }
        }
        if (!split) {
            dhr = hr;
            dmin = min;
            dsec = sec;
            dfract = fract;
        }
    }


    public static void main(String[] args) throws ModelException {
        List<CustomAction> customActions = new ArrayList<CustomAction>();
        CustomAction assign =
                new CustomAction("org.finra.datagenerator",
                        "assign", SetAssignExtension.SetAssignTag.class);
        customActions.add(assign);

        CustomAction range =
                new CustomAction("org.finra.datagenerator",
                        "range", RangeExtension.RangeTag.class);
        customActions.add(range);

        CustomAction file =
                new CustomAction("org.finra.datagenerator",
                        "file", FileExtension.FileTag.class);
        customActions.add(file);

        CustomAction transform =
                new CustomAction("org.finra.datagenerator",
                        "transform", InLineTransformerExtension.TransformTag.class);
        customActions.add(transform);

        SCXMLReader.Configuration configuration =
                new SCXMLReader.Configuration(null, null, customActions);


        AbstractStateMachine gen = new AbstractStateMachine(
                EventGenerator.class.getClassLoader().getResource("samplemachine.xml"),
                configuration) {
        };

        SCXML stateMachine = gen.getEngine().getStateMachine();

        gen.go();

        Map<String, TransitionTarget> targets = stateMachine.getTargets();

        SimpleTransition transition = stateMachine.getInitialTransition();


        List<EnterableState> states = stateMachine.getChildren();

        Set<String> variables = new HashSet<>();
        for (EnterableState state: states){
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

    }

    public static SCXML parse(final Reader scxmlReader, final List<CustomAction> customActions) throws Exception {
        SCXMLReader.Configuration configuration = new SCXMLReader.Configuration(null, null, customActions);
        return SCXMLReader.read(scxmlReader, configuration);
    }

/*
    private Map<String, String> fillInitialVariables() {
        Map<String, TransitionTarget> targets = model.getChildren();

        Set<String> variables = new HashSet<>();
        for (TransitionTarget target : targets.values()) {
            OnEntry entry = target.getOnEntry();
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

        Map<String, String> result = new HashMap<>();
        for (String variable : variables) {
            result.put(variable, "");
        }

        return result;
    }
*/
}

