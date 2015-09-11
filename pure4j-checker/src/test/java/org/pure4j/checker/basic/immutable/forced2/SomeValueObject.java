package org.pure4j.checker.basic.immutable.forced2;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;

/**
 * Broken immutable value implementation, but it's forced good.
 */
@ImmutableValue
public final class SomeValueObject {

	@ImmutableValue(Enforcement.FORCE)
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
	public String toString() {
		return Pure4J.toString(this, someArray);
	}
	
	
	
}
