package org.pure4j.checker.basic.immutable.forced;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;

/**
 * Broken immutable value implementation, but it's forced good.
 */
@ImmutableValue(Enforcement.FORCE)
public final class SomeValueObject {

	private final int[] someArray;

	public int[] getSomeArray() {
		return someArray;
	}

	public SomeValueObject(int[] someArray) {
		super();
		this.someArray = someArray;
	}

	@Override
	public int hashCode() {
		return Pure4J.hashCode(someArray);
	}

	@Override
	public boolean equals(Object obj) {
		return Pure4J.equals(obj, this);
	}

	@Override
	public String toString() {
		return Pure4J.toString(this, someArray);
	}
	
	
	
}
