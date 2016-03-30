package org.pure4j.test.checker.cf;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.unknown.Mutable;
import org.pure4j.test.CausesError;

public class ImmutableMisuse {

	@CausesError(code= { "[assignment.type.incompatible]" })
	@ImmutableValue String shouldBeImmutable = getMutable();
	
	@CausesError(code= { "[assignment.type.incompatible]" })
	@ImmutableValue Object shouldAlsoBeImmutable = new Object();
	
	@ImmutableValue Object someImmutable = getAnImmutable();
	
	@CausesError(code= { "[return.type.incompatible]" })
	private @ImmutableValue Object getAnImmutable() {
		return new Object();
	}
	
	private @Mutable String getMutable() {
		return "some string";
	}

	private void callWithImmutable(@ImmutableValue Object iv) {
		// null
	}
	
	@CausesError(code= { "[argument.type.incompatible]" })
	public void caller() {
		callWithImmutable(new Object());
	}
	
}
