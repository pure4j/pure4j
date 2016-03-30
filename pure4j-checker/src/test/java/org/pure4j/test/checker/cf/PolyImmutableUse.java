package org.pure4j.test.checker.cf;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.immutable.PolyImmutableValue;
import org.pure4j.annotations.unknown.Mutable;

public class PolyImmutableUse {

	@PolyImmutableValue Object value = "hello";
	
	@ImmutableValue Object immutable = getImmutable("hello");
	
	@Mutable Object mutable = getImmutable(new Object());

	private @PolyImmutableValue Object getImmutable(@PolyImmutableValue Object in) {
		return in;
	}
}
