package com.solrup.evtgen.annotation;

public @interface State {
	public final String INITIAL = "__initial__";
	public final String FINAL = "__final__";
	
	public String id() default INITIAL;
	public boolean condition() default false;
	public String[] requires() default {};
	public String target() default "";
}
