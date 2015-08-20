package org.pure4j.checker.not_final;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public class NotFinal {

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public String toString() {
		return null;
	}

	
}
