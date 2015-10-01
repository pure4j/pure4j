package org.pure4j.test.checker.spec.pure.implementation_only;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.ImpureCodeCallingPureCodeWithoutInterfacePurity;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.test.checker.support.CausesError;

public class SomeImplementationOnlyPure {

	@Pure
	@CausesError(PureMethodParameterNotImmutableException.class)
	public String implementationOnlyPure(Object o) {
		return "a"+"b";
	}
	
	@CausesError(ImpureCodeCallingPureCodeWithoutInterfacePurity.class)
	public String outsidePure() {
		return implementationOnlyPure(new Object());
	}
}
