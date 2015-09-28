package org.pure4j.checker.spec.mutable_unshared.inherit;

import org.pure4j.checker.support.ShouldBePure;


public class InheritedPure extends BasePure {

	@ShouldBePure
	public int onInheritedPure() {
		return onBasePure();
	}
}
