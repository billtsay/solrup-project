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
package com.solrup.evtgen.core;

import com.solrup.evtgen.StateMachine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml2.*;
import org.apache.commons.scxml2.env.SimpleDispatcher;
import org.apache.commons.scxml2.env.SimpleErrorReporter;
import org.apache.commons.scxml2.env.jexl.JexlContext;
import org.apache.commons.scxml2.env.jexl.JexlEvaluator;
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class AbstractStateMachine implements StateMachine {

    private static final Class<?>[] SIGNATURE = new Class[0];
    private static final Object[] PARAMETERS = new Object[0];
    private SCXML stateMachine;
    private SCXMLExecutor engine;
    private Log log;

    public AbstractStateMachine(final URL scxmlDocument,
                                SCXMLReader.Configuration configuration) throws ModelException {
        // default is JEXL
        this(scxmlDocument, new JexlContext(), new JexlEvaluator(), configuration);
    }

    public AbstractStateMachine(final URL scxmlDocument,
                                final Context rootCtx,
                                final Evaluator evaluator,
                                final SCXMLReader.Configuration configuration) throws ModelException {
        log = LogFactory.getLog(this.getClass());
        try {
            if (configuration == null) {
                stateMachine = SCXMLReader.read(scxmlDocument);
            } else {
                stateMachine = SCXMLReader.read(scxmlDocument, configuration);
            }
        } catch (IOException ioe) {
            logError(ioe);
        } catch (XMLStreamException xse) {
            logError(xse);
        } catch (ModelException me) {
            logError(me);
        }
        initialize(stateMachine, rootCtx, evaluator);
    }

    public AbstractStateMachine(final SCXML stateMachine) throws ModelException {
        // default is JEXL
        this(stateMachine, new JexlContext(), new JexlEvaluator());
    }

    public AbstractStateMachine(final SCXML stateMachine,
                                final Context rootCtx,
                                final Evaluator evaluator) throws ModelException {
        log = LogFactory.getLog(this.getClass());
        initialize(stateMachine, rootCtx, evaluator);
    }

    private void initialize(final SCXML stateMachine,
                            final Context rootCtx,
                            final Evaluator evaluator) throws ModelException {
        engine = new SCXMLExecutor(evaluator, new SimpleDispatcher(),
                new SimpleErrorReporter());
        engine.setStateMachine(stateMachine);
        engine.setRootContext(rootCtx);
    }

    public void addListener(SCXMLListener listener){
        engine.addListener(stateMachine, listener);
    }

    public void run(){
        try {
            engine.go();
        } catch (ModelException me) {
            logError(me);
        }
    }

    public boolean fireEvent(final String event, int type, Map<String, ?> payload) {
        return fireEvent(event);
    }

    public boolean fireEvent(final String event) {
        TriggerEvent[] evts = {new TriggerEvent(event,
                TriggerEvent.SIGNAL_EVENT)};
        try {
            engine.triggerEvents(evts);
        } catch (ModelException me) {
            logError(me);
        }
        return engine.getCurrentStatus().isFinal();
    }

    public SCXMLExecutor getEngine() {
        return engine;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(final Log log) {
        this.log = log;
    }

    public boolean resetMachine() {
        try {
            engine.reset();
        } catch (ModelException me) {
            logError(me);
            return false;
        }
        return true;
    }

    protected void logError(final Exception exception) {
        if (log.isErrorEnabled()) {
            log.error(exception.getMessage(), exception);
        }
    }
}

