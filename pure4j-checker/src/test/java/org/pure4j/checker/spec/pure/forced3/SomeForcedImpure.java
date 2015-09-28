package org.pure4j.checker.spec.pure.forced3;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodCallsImpureException;

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
