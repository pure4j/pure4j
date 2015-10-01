package org.pure4j.test.checker.spec.pure.forced3;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.test.checker.support.CausesError;

public class SomeForcedImpure {

	@Pure(Enforcement.NOT_PURE) 
	public int doSomethingNotPure() {
		return new Object().hashCode();
	}
	
	@Pure
	@CausesError(PureMethodCallsImpureException.class)
	public void callIt() {
		doSomethingNotPure();
	}
}
