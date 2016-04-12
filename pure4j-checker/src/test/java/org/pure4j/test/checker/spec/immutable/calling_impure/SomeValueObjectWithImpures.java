package org.pure4j.test.checker.spec.immutable.calling_impure;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class SomeValueObjectWithImpures {

	public static int someImpure() {
		return new Object().hashCode();
	}
	
	@CausesError(code ="pure4j.method_calls_impure")
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
