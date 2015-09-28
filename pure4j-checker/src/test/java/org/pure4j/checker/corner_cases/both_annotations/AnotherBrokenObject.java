package org.pure4j.checker.corner_cases.both_annotations;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.support.CausesError;
import org.pure4j.exception.ClassHasConflictingAnnotationsException;

@MutableUnshared
public abstract class AnotherBrokenObject implements BrokenValueObject {

	@CausesError(ClassHasConflictingAnnotationsException.class)
	public AnotherBrokenObject(Integer in) {
		super();
	}
	
}
