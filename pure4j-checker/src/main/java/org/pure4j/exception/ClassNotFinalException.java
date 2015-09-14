package org.pure4j.exception;

public class ClassNotFinalException extends Pure4JException {

	public ClassNotFinalException(Class<?> c) {
		super("Class "+c.getName()+" not declared final, but has @ImmutableValue annotation");
	}

}
