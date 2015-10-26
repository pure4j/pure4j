package org.pure4j.test.collections;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.test.ShouldBePure;

@ImmutableValue
public final class EasyCollider {
	
	final String someString;

	@ShouldBePure
	public EasyCollider(String someString) {
		super();
		this.someString = someString;
	}
	
	@ShouldBePure
	@Override
	public int hashCode() {
		return someString.hashCode() % 3;	// deliberately creating a small hash range
	}
	@ShouldBePure
	@Override
	public boolean equals(Object obj) {
		return this.someString.equals(((EasyCollider)obj).someString);
	}
	@ShouldBePure
	@Override
	public String toString() {
		return "["+someString+"]";
	}
	
}