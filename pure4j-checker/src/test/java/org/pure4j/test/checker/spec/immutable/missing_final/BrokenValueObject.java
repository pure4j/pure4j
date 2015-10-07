package org.pure4j.test.checker.spec.immutable.missing_final;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.PureMethodAccessesNonFinalFieldException;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

@ImmutableValue
public class BrokenValueObject extends AbstractBrokenObject {
	

	@ShouldBePure
	public BrokenValueObject(Integer in) {
		super(in);
	}

	@Override
	@ShouldBePure
	public int hashCode() {
		return 1;
	}

	@Override
	@ShouldBePure
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@CausesError({PureMethodAccessesNonFinalFieldException.class,PureMethodAccessesNonFinalFieldException.class} ) 
	public void increment() {
		this.in++;
	}

	@CausesError(PureMethodAccessesNonFinalFieldException.class) 
	public Integer getInt2() {
		return in;
	}

	@ShouldBePure
	public String toString() {
		return "";
	}
	
	
	
}
