package org.pure4j.checker.spec.mutable_unshared.bad_return;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.exception.PureMethodReturnNotImmutableException;

@MutableUnshared
public class Class1 {

	private int[] someState;
	
	@CausesError(PureMethodReturnNotImmutableException.class)
	public int[] returnStateSoEveryoneCanCorruptIt() {
		return someState;
	}
}
