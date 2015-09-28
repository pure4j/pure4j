package org.pure4j.checker.spec.pure.forced2;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

public class SomeForcedPure {

	@ShouldBePure
	@Pure(Enforcement.FORCE) 
	public int doSomethingNotPure() {
		return new Object().hashCode();
	}
	
	@Pure
	@ShouldBePure
	public void callIt() {
		doSomethingNotPure();
	}
}
