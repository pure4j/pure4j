package org.pure4j.checker.spec.pure.callingnotpure;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.exception.PureMethodCallsImpureException;

public class TestIllegalObjectUse {

	public int nonPureMethod() {
		return new Object().hashCode();
	}

	@Pure
	@CausesError(PureMethodCallsImpureException.class)
	public int doSomething2() {
		return nonPureMethod();
	}
	
	@Pure
	@CausesError({PureMethodCallsImpureException.class})
	public int doSomething1() {
		return new StringBuilder().hashCode();
	}
}
