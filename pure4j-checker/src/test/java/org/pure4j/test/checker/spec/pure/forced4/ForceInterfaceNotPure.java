package org.pure4j.test.checker.spec.pure.forced4;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.PureInterface;
import org.pure4j.exception.ImpureCodeCallingPureCodeWithoutInterfacePurity;
import org.pure4j.test.checker.support.CausesError;
import org.pure4j.test.checker.support.ShouldBePure;

/**
 * Broken immutable value implementation, but it's forced good.
 */
@ImmutableValue
public final class ForceInterfaceNotPure {

	@PureInterface(Enforcement.NOT_PURE)		// but, really should be pure
	public ForceInterfaceNotPure(String in) {
		super();
	}

	@ShouldBePure
	@Override
	public int hashCode() {
		return 1;
	}

	@ShouldBePure
	@Override
	public String toString() {
		return "";
	}
	
	
	@CausesError(ImpureCodeCallingPureCodeWithoutInterfacePurity.class)
	public static void someNotPureCode() {
		new ForceInterfaceNotPure("blah");
	}
	
	
	
}
