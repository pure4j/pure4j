package org.pure4j.test.checker.cf;

import org.pure4j.annotations.immutable.ImmutableValue;

public class ImmutableCallUse {
	
	public void takesImmutable(@ImmutableValue Object in) {
	}
	
	public SomeImmutable returnsImmutable() {
		return new SomeImmutable();
	}

	
	public @ImmutableValue Object someMethod() {
		takesImmutable(new SomeImmutable());
		@ImmutableValue Object out = returnsImmutable();
		return out;
	}
	
}

@ImmutableValue
class SomeImmutable {
	
	public SomeImmutable() {
	}
	
}

