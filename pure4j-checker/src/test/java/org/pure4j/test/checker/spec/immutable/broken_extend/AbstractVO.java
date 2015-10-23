package org.pure4j.test.checker.spec.immutable.broken_extend;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.test.CausesError;

@ImmutableValue
public abstract class AbstractVO {

	@CausesError(FieldNotFinalException.class)
	protected int bob = 6;

	
}
