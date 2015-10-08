package org.pure4j.test.checker.spec.pure.forced2;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.checker.support.ShouldBePure;

public class SomeForcedPure {

	@ShouldBePure
	@Pure(Enforcement.FORCE) 
	public static int doSomethingNotPure() {
		return new Object().hashCode();
	}
	
	@Pure
	@ShouldBePure
	public static void callIt() {
		doSomethingNotPure();
	}
}
