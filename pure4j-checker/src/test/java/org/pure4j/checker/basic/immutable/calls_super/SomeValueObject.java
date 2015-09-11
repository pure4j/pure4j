package org.pure4j.checker.basic.immutable.calls_super;

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
	public String toString() {
		return "";
	}
	
	
}
