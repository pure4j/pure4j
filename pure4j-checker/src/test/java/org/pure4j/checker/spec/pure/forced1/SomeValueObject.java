package org.pure4j.checker.spec.pure.forced1;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.ShouldBePure;

/**
 * Broken immutable value implementation, but it's forced good.
 */
@ImmutableValue
public final class SomeValueObject {

	@IgnoreImmutableTypeCheck
	private final int[] someArray;

	@ShouldBePure
	public int[] getSomeArray() {
		return someArray;
	}

	@ShouldBePure
	public SomeValueObject(int[] someArray) {
		super();
		this.someArray = someArray;
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return Pure4J.hashCode(someArray);
	}

	@ShouldBePure
	@Override
	public String toString() {
		return Pure4J.toString(this, someArray);
	}
	
	
	
}
