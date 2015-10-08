package org.pure4j.test.checker.spec.pure.callingnotinterfacepure;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.checker.support.ShouldBePure;

public class InterfaceNotPureOkCall {

	@ShouldBePure
	@Pure
	private static int doSomething2(Object o) {
		return 6;
	}
	
	@ShouldBePure
	@Pure
	public static int doSomething1() {
		return doSomething2(null);
	}
}
