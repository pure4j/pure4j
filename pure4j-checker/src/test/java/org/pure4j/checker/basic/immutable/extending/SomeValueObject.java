package org.pure4j.checker.basic.immutable.extending;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;

/**
 * Decent immutable value implementation.
 */
@ImmutableValue
public final class SomeValueObject extends AbstractVO {

	public SomeValueObject(int bob, String name) {
		super(bob);
		this.name = name;
	}

	private final String name;

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return Pure4J.equals(this, obj);
	}

	@Override
	public String toString() {
		return "hey";
	}
	
	
}
