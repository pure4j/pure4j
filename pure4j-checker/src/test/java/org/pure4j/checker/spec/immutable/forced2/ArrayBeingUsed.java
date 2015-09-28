package org.pure4j.checker.spec.immutable.forced2;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.ShouldBePure;

/**
 * Broken immutable value implementation, but it's forced good.
 */
@ImmutableValue
public final class ArrayBeingUsed {

	@IgnoreImmutableTypeCheck
	private final int[] someArray;

	@ShouldBePure
	public int[] getSomeArray() {
		return someArray;
	}

	@ShouldBePure
	@Pure(Enforcement.FORCE)
	public ArrayBeingUsed(int[] someArray) {
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
