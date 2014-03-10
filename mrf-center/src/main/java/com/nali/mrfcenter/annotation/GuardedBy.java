package com.nali.mrfcenter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Guarded by object monitor lock annotation,
 * it's just an indication that when access some field or method,
 * we should synchronized on some object monitor lock
 * @author will
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface GuardedBy {
	
	String lock();

}