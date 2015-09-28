package org.pure4j.checker.spec.immutable.calls_super;

import org.pure4j.checker.support.ShouldBePure;

public class AbstractVO {

	@ShouldBePure
	public String someStuff() {
		return "blah";
	}
}
