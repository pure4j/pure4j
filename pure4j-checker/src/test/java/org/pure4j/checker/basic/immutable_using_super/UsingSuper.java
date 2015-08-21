package org.pure4j.checker.basic.immutable_using_super;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public final class UsingSuper {

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return true;
	}

	@Override
	public String toString() {
		return "l";
	}

	
}
