package org.pure4j.checker.spec.immutable.broken_extend;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.exception.FieldNotFinalException;

@ImmutableValue
public abstract class AbstractVO {

	@CausesError(FieldNotFinalException.class)
	protected int bob = 6;
	
	
}
