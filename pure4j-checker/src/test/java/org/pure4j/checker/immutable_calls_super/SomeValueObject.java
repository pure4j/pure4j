package org.pure4j.checker.immutable_calls_super;

import java.math.BigInteger;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.pure_methods.SomePureStuff;
import org.pure4j.immutable.EqualsHelp;

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
		return EqualsHelp.equals(this, obj);
	}

	@Override
	public String toString() {
		return "";
	}
	
	
}
