package org.pure4j.checker.basic.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.pure4j.exception.Pure4JException;

@Retention(RetentionPolicy.RUNTIME)
public @interface CausesError {

	Class<? extends Pure4JException>[] value();
}
