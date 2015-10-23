package org.pure4j.test.checker.spec.immutable.calls_super;

import org.pure4j.test.ShouldBePure;

public class AbstractVO {

	@ShouldBePure
	public String someStuff() {
		return "blah";
	}
}
