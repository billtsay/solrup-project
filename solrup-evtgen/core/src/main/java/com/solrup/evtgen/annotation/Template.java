package com.solrup.evtgen.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Template {
	public String name();
	public String initial() default State.INITIAL;
	String environment();
	public boolean singleton() default false;
}
