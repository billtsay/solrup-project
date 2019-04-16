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

package org.finra.datagenerator.engine.scxml.tags;

import org.apache.commons.logging.Log;
import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCInstance;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.ModelException;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Implementation of dg:range tag
 */
public class TimerExtension implements CustomTagExtension<TimerExtension.TimerTag> {

    public Class<TimerTag> getTagActionClass() {
        return TimerTag.class;
    }

    public String getTagName() {
        return "timer";
    }

    public String getTagNameSpace() {
        return "org.finra.datagenerator";
    }

    /**
     * Performs variable assignments from a set of values
     *
     * @param action            a RangeTag Action
     * @param possibleStateList a current list of possible states produced so far from expanding a model state
     * @return the cartesian product of every current possible state and the set of values specified by action
     */
    public List<Map<String, String>> pipelinePossibleStates(TimerTag action,
                                                            List<Map<String, String>> possibleStateList) {
        String variable = action.getName();
        int freq = Integer.parseInt(action.getFrequency());
        int duration = Integer.parseInt(action.getDuration()) / freq;
        long millis = System.currentTimeMillis();

        List<Long> timerValues = new ArrayList<>();
        for (int n = 0; n < freq; ++n) {
            timerValues.add(millis + n * duration * 1000L);
        }

        //take the product
        List<Map<String, String>> productTemp = new LinkedList<>();
        for (Map<String, String> p : possibleStateList) {
            for (Long value : timerValues) {
                HashMap<String, String> n = new HashMap<>(p);
                n.put(variable, value.toString());
                productTemp.add(n);
            }
        }

        return productTemp;
    }

    /**
     * A custom Action for the 'dg:range' tag inside models
     */
    public static class TimerTag extends Action {
        private String name;
        private String duration;
        private String frequency;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        /**
         * Required implementation of an abstract method in Action
         *
         * @param eventDispatcher unused
         * @param errorReporter   unused
         * @param scInstance      unused
         * @param log             unused
         * @param collection      unused
         * @throws ModelException           never
         * @throws SCXMLExpressionException never
         */
        public void execute(EventDispatcher eventDispatcher, ErrorReporter errorReporter, SCInstance scInstance,
                            Log log, Collection collection) throws ModelException, SCXMLExpressionException {
            //Handled manually
        }
    }

}
