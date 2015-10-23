package org.pure4j.test.checker.spec.immutable.missing_final;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.PureMethodAccessesNonFinalFieldException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

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
	
	@ShouldBePure
	public void increment() {
		this.in++;
	}

	@ShouldBePure
	public Integer getInt2() {
		return in;
	}

	@ShouldBePure
	public String toString() {
		return "";
	}
	
	
	
}
