package org.pure4j.test.checker.spec.pure.callingnotpure;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.test.checker.support.CausesError;

public class TestIllegalObjectUse {

	public static int nonPureMethod() {
		return new Object().hashCode();
	}

	@Pure
	@CausesError(PureMethodCallsImpureException.class)
	public static int doSomething2() {
		return nonPureMethod();
	}
	
	@Pure
	@CausesError({PureMethodCallsImpureException.class})
	public static int doSomething1() {
		return new StringBuilder().hashCode();
	}
}
