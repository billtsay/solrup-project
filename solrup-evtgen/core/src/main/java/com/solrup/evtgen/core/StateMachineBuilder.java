package com.solrup.evtgen.core;

import com.solrup.evtgen.Environment;
import com.solrup.evtgen.StateMachine;
import com.solrup.evtgen.annotation.State;
import com.solrup.evtgen.annotation.Template;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.scxml2.Context;
import org.apache.commons.scxml2.Evaluator;
import org.apache.commons.scxml2.SCXMLListener;
import org.apache.commons.scxml2.env.jexl.JexlContext;
import org.apache.commons.scxml2.env.jexl.JexlEvaluator;
import org.apache.commons.scxml2.io.SCXMLReader;
import org.apache.commons.scxml2.model.EnterableState;
import org.apache.commons.scxml2.model.Transition;
import org.apache.commons.scxml2.model.TransitionTarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateMachineBuilder {
    final Context   DEFAULT_CONTEXT   = new JexlContext();
    final Evaluator DEFAULT_EVALUATOR = new JexlEvaluator();

    Context   context   = DEFAULT_CONTEXT;
    Evaluator evaluator = DEFAULT_EVALUATOR;

    SCXMLReader.Configuration configuration;

    Class template;

    Configurations configurations;

    public StateMachineBuilder(){
        configurations = new Configurations();
    }

    public StateMachineBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public StateMachineBuilder setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    public StateMachineBuilder setConfiguration(SCXMLReader.Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    public StateMachineBuilder setTemplate(Class template) {
        this.template = template;
        return this;
    }

    public StateMachine build() throws Exception {
        Environment environment = buildEnvironment(template);
        if (!template.isAnnotationPresent(Template.class)) {
            System.out.println("Error");
        }

        Template t = (Template) template.getAnnotation(Template.class);

        AbstractStateMachine stateMachine = new AbstractStateMachine(
                getClass().getClassLoader().getResource(t.environment().concat(".xml")), null);

        stateMachine.addListener(new EntryListener(template));

        return stateMachine;
    }

    enum TYPE {
        ONENTRY, ONEXIT, ONTRANS;
    }

    Map<TYPE, Set<Method>> buildMethodMap(Class template){
        Set<Method> onEntry = new HashSet<Method>();
        Set<Method> onExit = new HashSet<Method>();
        Set<Method> onTrans = new HashSet<Method>();

        for (Method m : template.getDeclaredMethods()){
            if (m.isAnnotationPresent(State.class)){
                State state = m.getAnnotation(State.class);
                if (state.on().equals(State.ON.ENTRY)){
                    onEntry.add(m);
                }
                if (state.on().equals(State.ON.EXIT)){
                    onExit.add(m);
                }
            }

            if (m.isAnnotationPresent(com.solrup.evtgen.annotation.Transition.class)){
                onTrans.add(m);
            }
        }

        Map<TYPE, Set<Method>> map = new HashMap<TYPE, Set<Method>>();
        map.put(TYPE.ONENTRY, onEntry);
        map.put(TYPE.ONEXIT, onExit);
        map.put(TYPE.ONTRANS, onTrans);

        return map;
    }

    protected Environment buildEnvironment(Class template){
        return new Environment() {
            @Override
            public Object context() {
                return null;
            }

            @Override
            public Logger logger() {
                return Logger.getLogger(template.getName());
            }
        };
    }

    protected Object buildInstance(Class template)throws IllegalAccessException, InstantiationException{
        return template.newInstance();
    }

    class EntryListener implements SCXMLListener {
        Environment environment;
        Object instance;

        EntryListener(Class template) {
            this.environment = buildEnvironment(template);
            try {
                instance = buildInstance(template);
            } catch (IllegalAccessException | InstantiationException e) {
                this.environment.logger().log(Level.SEVERE, e.getClass().getCanonicalName(), e);
            }
        }

        public void onEntry(EnterableState entered) {
            Set<Method> set = findStateMethods(entered, State.ON.ENTRY);
            for (Method m : set) invoke(m);
        }

        public void onTransition(TransitionTarget from,
                                 TransitionTarget to,
                                 Transition transition,
                                 String event) {
        }

        public void onExit(EnterableState exited) {
            Set<Method> set = findStateMethods(exited, State.ON.EXIT);
            for (Method m : set) invoke(m);
        }

        boolean invoke(Method method) {
            try {
                method.invoke(instance, environment);
            } catch (Exception e) {
                environment.logger().log(Level.SEVERE, e.getClass().getCanonicalName(), e);
                return false;
            }

            return true;
        }

        Set<Method> findStateMethods(EnterableState state, State.ON type){
            Set<Method> set = new HashSet<Method>();

            for (Method m : template.getDeclaredMethods()){
                if (m.isAnnotationPresent(State.class)){
                    State[] ss = m.getAnnotationsByType(State.class);
                    for (State s : ss) {
                        if (s.id().equals(state.getId())) {
                            if (type.equals(s.on())) {
                                set.add(m);
                            }
                        }
                    }
                }
            }

            return set;
        }

        Set<Method> findTransitionMethods(Transition trans){
            Set<Method> set = new HashSet<Method>();

            for (Method m : template.getDeclaredMethods()){
                if (m.isAnnotationPresent(com.solrup.evtgen.annotation.Transition.class)){
                    com.solrup.evtgen.annotation.Transition[] ts
                            = m.getAnnotationsByType(com.solrup.evtgen.annotation.Transition.class);
                    for (com.solrup.evtgen.annotation.Transition t : ts) {
                        if (t.event().equals(trans.getEvent())) {
                            set.add(m);
                        }
                    }
                }
            }

            return set;
        }
    }

    public static void main(String[] args) {
        StateMachineBuilder builder = new StateMachineBuilder();
        builder = builder.setTemplate(EventGenerator.class);

        try {
            StateMachine stateMachine = builder.build();
            stateMachine.run();


            stateMachine.fireEvent(EventGenerator.EVENT_START);
            stateMachine.fireEvent(EventGenerator.EVENT_SPLIT);
            stateMachine.fireEvent(EventGenerator.EVENT_UNSPLIT);
            stateMachine.fireEvent(EventGenerator.EVENT_STOP);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
