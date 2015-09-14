package org.pure4j.checker.basic.generics.holder_immutable;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.ShouldBePure;

@ImmutableValue
public final class GenericHolder<X extends SomeImmutable> {

	private final X in;

	@ShouldBePure
	public GenericHolder(X in) {
		super();
		this.in = in;
	}

	@ShouldBePure
	public X getIn() {
		return in;
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return Pure4J.hashCode(in);
	}

	

	@Override
	@ShouldBePure
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericHolder<?> other = (GenericHolder<?>) obj;
		if (in == null) {
			if (other.in != null)
				return false;
		} else if (!in.equals(other.in))
			return false;
		return true;
	}

	@Override
	@ShouldBePure
	public String toString() {
		return Pure4J.toString(this, in);
	}
	
	
	
}
