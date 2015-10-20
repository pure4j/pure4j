package org.pure4j.test.checker.spec.pure.forced1;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.PureInterface;
import org.pure4j.test.checker.support.ShouldBePure;

/**
 * Broken immutable value implementation, but it's forced good.
 */
@ImmutableValue
public final class ForceInterfacePurity {

	@ShouldBePure
	@PureInterface(Enforcement.FORCE)
	public ForceInterfacePurity(int[] someArray) {
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
	
	
	
}
