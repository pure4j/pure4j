package org.pure4j.checker.spec.mutable_unshared.override;

import org.pure4j.checker.support.CausesError;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.exception.PureMethodCallsImpureException;

public class LeakyOverride extends BadBase {

	@Override
	@ShouldBePure
	public Integer somethingNotPure() {
		return (Integer) super.somethingNotPure();	
	}

	@CausesError(PureMethodCallsImpureException.class)
	@ShouldBePure
	public int somethingElse() {
		return (Integer) super.somethingNotPure();	
	}
	
}
