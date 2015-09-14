package org.pure4j.checker.basic.pure.callingnotpure;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.exception.PureMethodCallsImpureException;

public class TestIllegalObjectUse extends AbstractChecker {

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
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
