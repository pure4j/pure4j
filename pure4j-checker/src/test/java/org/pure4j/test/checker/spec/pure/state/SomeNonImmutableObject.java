package org.pure4j.test.checker.spec.pure.state;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.PureMethodOnNonImmutableClassException;
import org.pure4j.test.checker.spec.immutable.calls_super.SomeValueObject;
import org.pure4j.test.checker.support.CausesError;

public class SomeNonImmutableObject {

	public final SomeValueObject immutable;

	@CausesError(PureMethodOnNonImmutableClassException.class)
	@Pure
	public String returnSomeField() {
		return immutable.doSomething();
	}

	public SomeNonImmutableObject(SomeValueObject immutable) {
		super();
		this.immutable = immutable;
	}
}
