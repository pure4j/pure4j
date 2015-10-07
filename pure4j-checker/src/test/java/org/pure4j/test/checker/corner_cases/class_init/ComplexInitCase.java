package org.pure4j.test.checker.corner_cases.class_init;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.PureMethodAccessesNonImmutableFieldException;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

@ImmutableValue
public final class ComplexInitCase {
	
	final static int yellow = 5;

	static {
		System.out.println("This is going to be impure");
	}

	/**
	 * These annotations should be on the static, but they don't fit there.
	 */
	@CausesError({PureMethodCallsImpureException.class, PureMethodAccessesNonImmutableFieldException.class})
	@Override
	@ShouldBePure
	public int hashCode() {
		return 0;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return "";
	}

	
}
