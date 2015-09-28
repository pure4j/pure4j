package org.pure4j.checker.spec.immutable.non_pure_param;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.support.CausesError;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.exception.PureMethodParameterNotImmutableException;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class NonPureParameter {
	
	
	@CausesError(PureMethodParameterNotImmutableException.class)
	@ShouldBePure
	public NonPureParameter(Object o) {
		super();
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return 0;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return "hey";
	}
	
	
}
