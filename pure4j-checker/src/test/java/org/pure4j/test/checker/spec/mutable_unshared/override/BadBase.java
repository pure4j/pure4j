package org.pure4j.test.checker.spec.mutable_unshared.override;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.PureMethodReturnNotImmutableException;
import org.pure4j.test.CausesError;

@MutableUnshared
public abstract class BadBase {

	@CausesError(PureMethodReturnNotImmutableException.class)
	public Object somethingNotPure() {
		return new Object().hashCode();
	}
}
