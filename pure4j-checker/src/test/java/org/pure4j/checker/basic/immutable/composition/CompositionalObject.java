package org.pure4j.checker.basic.immutable.composition;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.immutable.good.SomeValueObject;
import org.pure4j.checker.basic.support.ShouldBePure;

@ImmutableValue
public final class CompositionalObject {
	
	private final SomeValueObject svo;

	public CompositionalObject(SomeValueObject svo) {
		super();
		this.svo = svo;
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return Pure4J.hashCode(svo);
	}

	@ShouldBePure
	public SomeValueObject getSvo() {
		return svo;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return Pure4J.toString(this, svo);
	}
	
	
}
