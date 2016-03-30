package org.pure4j.test.checker.cf;

import org.pure4j.annotations.immutable.ImmutableValue;

public class ImmutableUse {

	@ImmutableValue String shouldBeImmutable = "is immutable";
	
	@ImmutableValue int shouldAlsoBeImmutable = 54;
	
	@ImmutableValue Object someImmutable = getAnImmutable();
	
	private @ImmutableValue Object getAnImmutable() {
		return "someString";
	}
	
	private void callWithImmutable(@ImmutableValue Object iv) {
		// null
	}
	
	public void caller() {
		callWithImmutable(shouldBeImmutable);
	}
	
}
