package org.pure4j.test.checker.spec.generics.holder_immutable;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.ShouldBePure;

@ImmutableValue
public final class GenericHolderImm<X extends SomeImmutable> {

	private final X in;

	@ShouldBePure
	public GenericHolderImm(X in) {
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
		GenericHolderImm<?> other = (GenericHolderImm<?>) obj;
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
