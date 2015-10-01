package org.pure4j.test.checker.spec.immutable.calling_impure;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class SomeValueObjectWithImpures {

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
