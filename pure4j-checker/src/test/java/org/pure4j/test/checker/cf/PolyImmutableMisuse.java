package org.pure4j.test.checker.cf;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.immutable.PolyImmutableValue;
import org.pure4j.test.CausesError;

public class PolyImmutableMisuse {

	@CausesError(code= {"[assignment.type.incompatible]"})
	@ImmutableValue Object immutable = getImmutable(new Object(), "string");

	private @PolyImmutableValue Object getImmutable(@PolyImmutableValue Object in, @PolyImmutableValue Object in2) {
		return in;
	}
}
