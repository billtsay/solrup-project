package com.solrup.evtgen.annotation;

public @interface Transition {
	public String name();
	public String event();
	public String target() default "";
}
