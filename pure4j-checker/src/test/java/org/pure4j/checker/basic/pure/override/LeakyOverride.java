package org.pure4j.checker.basic.pure.override;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.exception.PureMethodCallsImpureException;

@Pure
public class LeakyOverride extends BadBase {

	@Override
	@CausesError(PureMethodCallsImpureException.class)
	public int somethingNotPure() {
		return super.somethingNotPure();	
	}

	@CausesError(PureMethodCallsImpureException.class)
	public int somethingElse() {
		return super.somethingNotPure();	
	}
	
}
