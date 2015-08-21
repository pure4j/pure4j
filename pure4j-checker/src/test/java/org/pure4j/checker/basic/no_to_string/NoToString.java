package org.pure4j.checker.basic.no_to_string;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public final class NoToString {

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

}
