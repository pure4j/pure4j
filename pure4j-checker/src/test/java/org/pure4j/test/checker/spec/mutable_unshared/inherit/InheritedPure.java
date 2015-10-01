package org.pure4j.test.checker.spec.mutable_unshared.inherit;

import org.pure4j.test.checker.support.ShouldBePure;


public class InheritedPure extends BasePure {

	@ShouldBePure
	public int onInheritedPure() {
		return onBasePure();
	}
}
