package org.pure4j.checker.spec.mutable_unshared.narrowing;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.support.CausesError;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.exception.PureMethodParameterNotImmutableException;

@MutableUnshared
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
