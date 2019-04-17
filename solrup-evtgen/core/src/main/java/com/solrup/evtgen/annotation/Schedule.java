package com.solrup.evtgen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Schedule {
	/*
	* * * * * *
	| | | | | |
	| | | | | +-- Year              (range: 1900-3000)
	| | | | +---- Day of the Week   (range: 1-7, 1 standing for Monday)
	| | | +------ Month of the Year (range: 1-12)
	| | +-------- Day of the Month  (range: 1-31)
	| +---------- Hour              (range: 0-23)
	+------------ Minute            (range: 0-59)
	 */
	// "6,12,18,24,30,36,42,48,54 * * * * *" = "*/6 * * * * *" occurrence per 6 minute.
	public String cron() default "*/6 * * * * *" ;
}
