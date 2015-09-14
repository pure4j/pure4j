package org.pure4j.checker.basic.immutable.missing_final;

import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.FieldNotFinalException;

public class AnotherBrokenObject {

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
