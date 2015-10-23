package org.pure4j.test.checker.spec.immutable.overriding;

import org.pure4j.Pure4J;
import org.pure4j.test.ShouldBePure;

public class SomeClass1 {

	@Override
	@ShouldBePure
	public int hashCode() {
		return 7;
	}

	@Override
	@ShouldBePure
	public String toString() {
		return Pure4J.toString(this);
	}

}
