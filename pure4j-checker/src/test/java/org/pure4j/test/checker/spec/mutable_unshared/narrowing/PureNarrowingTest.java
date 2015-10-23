package org.pure4j.test.checker.spec.mutable_unshared.narrowing;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

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
