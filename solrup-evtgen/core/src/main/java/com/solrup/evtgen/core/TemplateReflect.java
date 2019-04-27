package com.solrup.evtgen.core;

import com.solrup.evtgen.annotation.DataModel;
import com.solrup.evtgen.annotation.State;
import com.solrup.evtgen.annotation.Template;
import org.apache.commons.scxml2.model.EnterableState;
import org.apache.commons.scxml2.model.Transition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TemplateReflect {
    Class<?> template;
    Template annotation;

    public TemplateReflect(Class<?> template){
        this.template = template;
        annotation = template.getAnnotation(Template.class);
    }

    public boolean isSingleton(){
        return annotation.singleton();
    }

    public Map<String, Field> getDataModel(){
        Map<String, Field> fieldMap = new HashMap<String, Field>();

        for (Field f : template.getDeclaredFields()){
            if (f.isAnnotationPresent(DataModel.class)){
                fieldMap.put(f.getName(), f);
            }
        }

        return fieldMap;
    }

    public Set<Method> getMethods(EnterableState state, State.ON type){
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

    public Set<Method> getMethods(Transition trans){
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
