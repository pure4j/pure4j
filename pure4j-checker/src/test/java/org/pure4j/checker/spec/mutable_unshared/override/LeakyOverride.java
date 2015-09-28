package org.pure4j.checker.spec.mutable_unshared.override;

import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodCallsImpureException;

public class LeakyOverride extends BadBase {

	@Override
	@ShouldBePure
	public int somethingNotPure() {
		return super.somethingNotPure();	
	}

	@CausesError(PureMethodCallsImpureException.class)
	@ShouldBePure
	public int somethingElse() {
		return super.somethingNotPure();	
	}
	
}
