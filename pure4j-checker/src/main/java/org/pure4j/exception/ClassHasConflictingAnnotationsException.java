package org.pure4j.exception;

import org.pure4j.model.impl.ClassHandle;

public class ClassHasConflictingAnnotationsException extends Pure4JException {

	public ClassHasConflictingAnnotationsException(ClassHandle c) {
		super("Class "+c.getClassName()+" marked with both @ImmutableValue and @MutableUnshared annotations in it's hierarchy.");
	}

}
