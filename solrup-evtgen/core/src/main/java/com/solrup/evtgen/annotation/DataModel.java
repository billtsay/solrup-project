package com.solrup.evtgen.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Target;

@Target(FIELD)
public @interface DataModel {
	public String name();
}
