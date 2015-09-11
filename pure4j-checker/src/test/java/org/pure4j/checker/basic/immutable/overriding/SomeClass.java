package org.pure4j.checker.basic.immutable.overriding;

import org.pure4j.Pure4J;

public class SomeClass {

	@Override
	public int hashCode() {
		return 7;
	}

	@Override
	public String toString() {
		return Pure4J.toString(this);
	}

}
