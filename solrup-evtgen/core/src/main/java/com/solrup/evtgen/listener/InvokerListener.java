package com.solrup.evtgen.listener;

import com.solrup.evtgen.Environment;
import com.solrup.evtgen.annotation.State;
import com.solrup.evtgen.core.TemplateReflect;
import org.apache.commons.scxml2.SCXMLListener;
import org.apache.commons.scxml2.model.EnterableState;
import org.apache.commons.scxml2.model.Transition;
import org.apache.commons.scxml2.model.TransitionTarget;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;

public class InvokerListener implements SCXMLListener {
    Environment environment;
    Object instance;
    TemplateReflect template;

    public InvokerListener (TemplateReflect template, Environment<?> environment, Object instance) {
        this.template = template;
        this.environment = environment;
        this.instance = instance;
    }

    public void onEntry(EnterableState entered) {
        Set<Method> set = template.getMethods(entered, State.ON.ENTRY);
        for (Method m : set) invoke(m);
    }

    public void onTransition(TransitionTarget from,
                             TransitionTarget to,
                             Transition transition,
                             String event) {
    }

    public void onExit(EnterableState exited) {
        Set<Method> set = template.getMethods(exited, State.ON.EXIT);
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
}
