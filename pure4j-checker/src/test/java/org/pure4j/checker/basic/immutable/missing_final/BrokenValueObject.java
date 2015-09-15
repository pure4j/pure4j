package org.pure4j.checker.basic.immutable.missing_final;

import java.io.InputStream;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.exception.PureMethodCallsImpureException;

@ImmutableValue
public class BrokenValueObject extends AnotherBrokenObject {

	@CausesError(FieldNotFinalException.class)
	Integer int2;
	@CausesError(FieldNotFinalException.class)
	String b;
	@CausesError(FieldTypeNotImmutableException.class)
	final InputStream is;
	
	@CausesError(PureMethodParameterNotImmutableException.class)
	public BrokenValueObject(Integer in, Integer int2, String b, InputStream is) {
		super(in);
		this.int2 = int2;
		this.b = b;
		this.is = is;
	}

	@CausesError(PureMethodCallsImpureException.class)
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((int2 == null) ? 0 : int2.hashCode());
		result = prime * result + ((is == null) ? 0 : is.hashCode());
		return result;
	}

	@Override
	@ShouldBePure
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@ShouldBePure // pure, just not immutable
	public void increment() {
		this.int2++;
	}

	@ShouldBePure
	public Integer getInt2() {
		return int2;
	}

	@ShouldBePure
	public String getB() {
		return b;
	}

	@ShouldBePure
	public InputStream getIs() {
		return is;
	}
	
	
}
