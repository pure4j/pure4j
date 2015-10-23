package org.pure4j.test.checker.spec.mutable_unshared.runtime_narrow;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.PureMethodReturnNotImmutableException;
import org.pure4j.test.CausesError;

@MutableUnshared
public class BrokenRuntimeCheck {


	@CausesError(PureMethodReturnNotImmutableException.class)
	public Object in() {
		if (new Object().equals(5)) {
			return 5;
		} else {
			return Pure4J.returnImmutable(66);
		}
	}
}
