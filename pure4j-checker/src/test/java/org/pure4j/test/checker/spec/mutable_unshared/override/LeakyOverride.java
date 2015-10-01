package org.pure4j.test.checker.spec.mutable_unshared.override;

import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

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
