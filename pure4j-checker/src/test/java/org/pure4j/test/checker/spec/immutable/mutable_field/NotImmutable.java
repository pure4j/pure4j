package org.pure4j.test.checker.spec.immutable.mutable_field;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

@ImmutableValue
public class NotImmutable {

	@CausesError(FieldTypeNotImmutableException.class)
	protected final Object bob;

	@ShouldBePure
	public Object getBob() {
		return bob;
	}

	@Override
	@ShouldBePure
	public int hashCode() {
		return 1;
	}

	@Override
	@ShouldBePure
	public String toString() {
		return null;
	}

	@ShouldBePure
	public NotImmutable(int bob) {
		super();
		this.bob = bob;
	}
	
}
