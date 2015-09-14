package org.pure4j.checker.basic.immutable.calling_impure;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodCallsImpureException;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class SomeValueObject {

	public static int someImpure() {
		return new Object().hashCode();
	}
	
	@CausesError(PureMethodCallsImpureException.class)
	public int someMethodThatShouldBePure() {
		return someImpure();
	}
	
	@Override
	@ShouldBePure
	public int hashCode() {
		return 0;
	}

	@Override
	@ShouldBePure
	public String toString() {
		return "hey";
	}
	
	
}
