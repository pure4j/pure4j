package org.pure4j.checker.spec.pure.state;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.checker.spec.immutable.calls_super.SomeValueObject;

public class SomeNonPureObject {

	public final SomeValueObject immutable;

	@ShouldBePure
	@Pure
	public String returnSomeField() {
		return immutable.doSomething();
	}

	public SomeNonPureObject(SomeValueObject immutable) {
		super();
		this.immutable = immutable;
	}
}
