package org.pure4j.checker.spec.immutable.array;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.FieldTypeNotImmutableException;

@ImmutableValue
public final class HasPrivateArray {

	/*
	 * Can't be immutable, as this can be changed.
	 */
	@CausesError({FieldTypeNotImmutableException.class})
	private final int[] members = new int[] { 4, 5, 6};

	@Override
	@ShouldBePure
	public int hashCode() {
		return 5;
	}

	@Override
	@ShouldBePure
	public boolean equals(Object obj) {
		return true;
	}

	@Override
	@ShouldBePure
	public String toString() {
		return "blah";
	}
	
	
}
