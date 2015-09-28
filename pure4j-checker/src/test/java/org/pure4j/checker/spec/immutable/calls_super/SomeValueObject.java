package org.pure4j.checker.spec.immutable.calls_super;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.support.ShouldBePure;

@ImmutableValue
public final class SomeValueObject extends AbstractVO {

	@ShouldBePure
	public String doSomething() {
		return  super.someStuff();
	}
	
	@ShouldBePure
	@Override
	public int hashCode() {
		return 0;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return "";
	}
	
	
}
