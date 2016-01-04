package org.pure4j.exception;

import org.pure4j.model.impl.ClassHandle;

public class ClassNotFinalException extends Pure4JException {

	public ClassNotFinalException(ClassHandle c) {
		super("Class "+c.getClassName()+" not declared final, but has @ImmutableValue / @MutableUnshared annotation");
	}

}
