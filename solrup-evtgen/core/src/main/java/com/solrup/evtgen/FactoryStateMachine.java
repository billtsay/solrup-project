package com.solrup.evtgen;

public interface FactoryStateMachine extends StateMachine {
    StateMachine create();
}
