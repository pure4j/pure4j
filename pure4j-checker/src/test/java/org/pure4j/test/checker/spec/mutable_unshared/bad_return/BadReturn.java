package org.pure4j.test.checker.spec.mutable_unshared.bad_return;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.PureMethodReturnNotImmutableException;
import org.pure4j.test.checker.support.CausesError;

@MutableUnshared
public class BadReturn {

	private int[] someState;
	
	@CausesError(PureMethodReturnNotImmutableException.class)
	public int[] returnStateSoEveryoneCanCorruptIt() {
		return someState;
	}
}
