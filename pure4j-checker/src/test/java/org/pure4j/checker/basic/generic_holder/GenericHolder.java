package org.pure4j.checker.basic.generic_holder;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.immutable.EqualsHelp;
import org.pure4j.immutable.HashHelp;
import org.pure4j.immutable.StringHelp;

@ImmutableValue
public final class GenericHolder<X> {

	private final X in;

	public GenericHolder(X in) {
		super();
		this.in = in;
	}

	public X getIn() {
		return in;
	}

	@Override
	public int hashCode() {
		return HashHelp.hashCode(in);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsHelp.equals(this, obj, this.in);
	}

	@Override
	public String toString() {
		return StringHelp.toString(this, in);
	}
	
	
	
}
