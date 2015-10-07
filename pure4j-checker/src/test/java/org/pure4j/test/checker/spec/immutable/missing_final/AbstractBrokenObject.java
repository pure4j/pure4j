package org.pure4j.test.checker.spec.immutable.missing_final;

import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.exception.PureMethodAccessesNonFinalFieldException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

public abstract class AbstractBrokenObject {

	@CausesError(FieldNotFinalException.class)
	protected Integer in;

	
	@CausesError(PureMethodAccessesNonFinalFieldException.class) 
	public Integer getIn() {
		return in;
	}

	@CausesError(PureMethodAccessesNonFinalFieldException.class) 
	public void setIn(Integer in) {
		this.in = in;
	}

	@CausesError(PureMethodAccessesNonFinalFieldException.class) 
	public AbstractBrokenObject(Integer in) {
		super();
		this.in = in;
	}
	
}
