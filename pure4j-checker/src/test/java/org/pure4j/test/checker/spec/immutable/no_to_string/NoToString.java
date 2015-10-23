package org.pure4j.test.checker.spec.immutable.no_to_string;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.ShouldBePure;

@ImmutableValue
public final class NoToString {

	@ShouldBePure
	@Override
	public int hashCode() {
		return 1;
	}

	@ShouldBePure
	@Override
	public boolean equals(Object obj) {
		return false;
	}

}
