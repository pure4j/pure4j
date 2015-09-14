package org.pure4j.checker.basic.immutable.extending;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.ShouldBePure;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class SomeValueObject extends AbstractVO {

	@ShouldBePure
	public SomeValueObject(int bob, String name) {
		super(bob);
		this.name = name;
	}

	private final String name;

	@ShouldBePure
	public String getName() {
		return name;
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return 0;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return "hey";
	}
	
	
}
