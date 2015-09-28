package org.pure4j.checker.spec.pure.callingnotinterfacepure;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.ShouldBePure;

public class InterfaceNotPureOkCall {

	@ShouldBePure
	@Pure
	private int doSomething2(Object o) {
		return 6;
	}
	
	@ShouldBePure
	@Pure
	public int doSomething1() {
		return doSomething2(null);
	}
}
