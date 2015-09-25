package org.pure4j.checker.spec.generics.holder_object;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.exception.PureMethodParameterNotImmutableException;

@ImmutableValue
public final class GenericHolderX<X> {

	@CausesError(FieldTypeNotImmutableException.class)
	private final X in;

	@CausesError(PureMethodParameterNotImmutableException.class)
	@ShouldBePure
	public GenericHolderX(X in) {
		super();
		this.in = in;
	}

	@ShouldBePure
	public X getIn() {
		return in;
	}

	@Override
	@ShouldBePure
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
		GenericHolderX<?> other = (GenericHolderX<?>) obj;
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
