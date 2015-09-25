package org.pure4j.checker.spec.immutable.mutable_field;

import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.FieldTypeNotImmutableException;

public class NotImmutable {

	@CausesError(FieldTypeNotImmutableException.class)
	protected final Object bob;

	@ShouldBePure
	public Object getBob() {
		return bob;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public String toString() {
		return null;
	}

	@ShouldBePure
	public NotImmutable(int bob) {
		super();
		this.bob = bob;
	}
	
}
