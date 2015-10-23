package org.pure4j.test.checker.spec.immutable.composition;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.spec.immutable.good.SomeGoodValueObject;

@ImmutableValue
public final class CompositionalObject {
	
	private final SomeGoodValueObject svo;

	@ShouldBePure
	public CompositionalObject(SomeGoodValueObject svo) {
		super();
		this.svo = svo;
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return Pure4J.hashCode(svo);
	}

	@ShouldBePure
	public SomeGoodValueObject getSvo() {
		return svo;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return Pure4J.toString(this, svo);
	}
	
	
}
