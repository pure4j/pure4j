package org.pure4j.test.checker.spec.immutable.array;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

import com.google.common.hash.HashingOutputStream;

@ImmutableValue
public final class HasPrivateArray {

	/*
	 * Can't be immutable, as this can be changed.
	 */
	@CausesError({FieldTypeNotImmutableException.class, HashingOutputStream.class})
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
