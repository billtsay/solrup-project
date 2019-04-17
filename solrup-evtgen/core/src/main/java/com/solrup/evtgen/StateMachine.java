package com.solrup.evtgen;

import org.apache.commons.scxml2.SCXMLListener;

import java.util.Map;

public interface StateMachine {
    void addListener(SCXMLListener listener);
    boolean fireEvent(String name);
    boolean fireEvent(String name, int type, Map<String, ?> payload);
    void run();
}
