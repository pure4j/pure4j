package org.pure4j.checker.basic.immutable.array;

import java.util.Arrays;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public final class HasPrivateArray {

	/*
	 * Can't be immutable, as this can be changed.
	 */
	private final int[] members = new int[] { 4, 5, 6};

	@Override
	public int hashCode() {
		return 5;
	}

	@Override
	public boolean equals(Object obj) {
		return true;
	}

	@Override
	public String toString() {
		return "blah";
	}
	
	
}
