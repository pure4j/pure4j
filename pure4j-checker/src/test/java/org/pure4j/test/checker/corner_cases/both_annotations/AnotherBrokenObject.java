package org.pure4j.test.checker.corner_cases.both_annotations;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.ClassHasConflictingAnnotationsException;
import org.pure4j.test.checker.support.CausesError;

@MutableUnshared
public abstract class AnotherBrokenObject implements BrokenValueObject {

	@CausesError(ClassHasConflictingAnnotationsException.class)
	public AnotherBrokenObject(Integer in) {
		super();
	}
	
}
