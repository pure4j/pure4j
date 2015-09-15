package org.pure4j.checker.basic.pure.interfacenotpure;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.exception.PureMethodParameterNotImmutableException;

public class TestCheckCatchesNonImmutableValueArguments extends AbstractChecker {

	@Pure
	@CausesError(PureMethodParameterNotImmutableException.class)
	public int someFunction(InputStream is) {
		return 0;
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
