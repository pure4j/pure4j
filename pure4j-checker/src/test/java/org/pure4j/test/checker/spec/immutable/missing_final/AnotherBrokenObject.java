package org.pure4j.test.checker.spec.immutable.missing_final;

import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

public abstract class AnotherBrokenObject {

	@CausesError(FieldNotFinalException.class)
	protected Integer in;

	
	@ShouldBePure
	public Integer getIn() {
		return in;
	}

	@ShouldBePure
	public void setIn(Integer in) {
		this.in = in;
	}

	@ShouldBePure
	public AnotherBrokenObject(Integer in) {
		super();
		this.in = in;
	}
	
}
