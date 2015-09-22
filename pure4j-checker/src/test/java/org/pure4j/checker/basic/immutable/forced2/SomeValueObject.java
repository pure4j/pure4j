package org.pure4j.checker.basic.immutable.forced2;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreNonImmutableTypeCheck;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

/**
 * Broken immutable value implementation, but it's forced good.
 */
@ImmutableValue
public final class SomeValueObject {

	@IgnoreNonImmutableTypeCheck
	private final int[] someArray;

	@IgnoreNonImmutableTypeCheck
	@ShouldBePure
	public int[] getSomeArray() {
		return someArray;
	}

	@ShouldBePure
	@Pure(Enforcement.FORCE)
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
