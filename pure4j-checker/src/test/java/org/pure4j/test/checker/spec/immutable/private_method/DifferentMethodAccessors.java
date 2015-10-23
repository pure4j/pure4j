package org.pure4j.test.checker.spec.immutable.private_method;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

@MutableUnshared
public class DifferentMethodAccessors {

	@CausesError(PureMethodParameterNotImmutableException.class)
	public void process1(Object in) {
		return;
	}

	@ShouldBePure
	protected void process2(Object in) {
		return;
	}
	
	@CausesError(PureMethodParameterNotImmutableException.class)
	void process3(Object in) {
		return;
	}
	
	@ShouldBePure
	private void process4(Object in) {
		return;
	}
}
