package org.pure4j.checker.immutable_composition;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.good.SomeValueObject;
import org.pure4j.immutable.EqualsHelp;
import org.pure4j.immutable.HashHelp;
import org.pure4j.immutable.StringHelp;

@ImmutableValue
public final class CompositionalObject {
	
	private final SomeValueObject svo;

	public CompositionalObject(SomeValueObject svo) {
		super();
		this.svo = svo;
	}

	@Override
	public int hashCode() {
		return HashHelp.hashCode(svo);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsHelp.equals(this, obj);
	}

	public SomeValueObject getSvo() {
		return svo;
	}

	@Override
	public String toString() {
		return StringHelp.toString(this, svo);
	}
	
	
}
