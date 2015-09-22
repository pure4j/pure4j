package org.pure4j.checker.basic.pure.narrowing;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodParameterNotImmutableException;

@ImmutableValue
public class PureNarrowingTest {

	@CausesError(PureMethodParameterNotImmutableException.class)
	public String doSomethingInterfaceNotPure(Object in) {
		return "blah";
	}
	
	@ShouldBePure
	public String doSomethingInterfacePure(String in) {
		return doSomethingInterfaceNotPure(in);
	}

	@Override
	@ShouldBePure
	public int hashCode() {
		return 0;
	}

	@Override
	@ShouldBePure
	public String toString() {
		return null;
	}
	
	
}
