package org.pure4j.checker.immutable_extending;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.immutable.EqualsHelp;

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
		return EqualsHelp.equals(this, obj);
	}

	@Override
	public String toString() {
		return "hey";
	}
	
	
}
