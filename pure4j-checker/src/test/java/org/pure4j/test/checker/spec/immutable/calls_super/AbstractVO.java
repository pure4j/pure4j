package org.pure4j.test.checker.spec.immutable.calls_super;

import org.pure4j.test.checker.support.ShouldBePure;

public class AbstractVO {

	@ShouldBePure
	public String someStuff() {
		return "blah";
	}
}
