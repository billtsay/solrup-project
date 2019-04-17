package com.solrup.evtgen.annotation;

public @interface StateMachine {
	public String name();
	public String initial() default State.INITIAL;
	String resource();
	public boolean singleton() default false;
}
