package org.pure4j.checker.basic.generics.holder_object;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;

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
		return Pure4J.hashCode(in);
	}

	@Override
	public boolean equals(Object obj) {
		return Pure4J.equals(this, obj);
	}

	@Override
	public String toString() {
		return Pure4J.toString(this, in);
	}
	
	
	
}
