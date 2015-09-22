package org.pure4j.exception;

public class ClassHasConflictingAnnotationsException extends Pure4JException {

	public ClassHasConflictingAnnotationsException(Class<?> c) {
		super("Class "+c.getName()+" marked with both @ImmutableValue and @MutableUnshared annotations in it's hierarchy.");
	}

}
