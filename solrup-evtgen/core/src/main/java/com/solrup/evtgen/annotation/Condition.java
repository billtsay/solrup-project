package com.solrup.evtgen.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

@Target(METHOD)
public @interface Condition {
	public Class<?> exception() default Exception.class;
	public String target();
}
