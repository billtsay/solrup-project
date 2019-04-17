package com.solrup.evtgen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface State {
	public final String INITIAL = "__initial__";
	public final String FINAL = "__final__";
	public enum ON{ENTRY, EXIT};
	
	public String id() default INITIAL;
	public ON  on() default ON.ENTRY;
	public boolean condition() default false;
	public String[] requires() default {};
	public String target() default "";
}
