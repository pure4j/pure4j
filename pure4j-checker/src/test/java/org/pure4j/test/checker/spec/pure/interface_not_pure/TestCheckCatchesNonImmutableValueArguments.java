package org.pure4j.test.checker.spec.pure.interface_not_pure;

import java.io.InputStream;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.test.CausesError;

public class TestCheckCatchesNonImmutableValueArguments {

	@Pure
	@CausesError(PureMethodParameterNotImmutableException.class)
	public int someFunction(InputStream is) {
		return 0;
	}

}
