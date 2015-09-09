package org.pure4j.checker.basic.immutable.calls_super;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public final class SomeValueObject extends AbstractVO {

	public String doSomething() {
		return  super.someStuff();
	}
	
	
	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return Pure4J.equals(this, obj);
	}

	@Override
	public String toString() {
		return "";
	}
	
	
}
