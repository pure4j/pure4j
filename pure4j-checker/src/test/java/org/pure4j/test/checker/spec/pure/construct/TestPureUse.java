package org.pure4j.test.checker.spec.pure.construct;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.checker.spec.immutable.calls_super.SomeValueObject;
import org.pure4j.test.checker.spec.mutable_unshared.classes.SomeMutableUnsharedClass;
import org.pure4j.test.checker.support.ShouldBePure;

public class TestPureUse {
	
	@ShouldBePure
	@Pure
	public int doSomething1() {
		return new SomeMutableUnsharedClass().somePureOperation(5);
	}
	
	@ShouldBePure
	@Pure
	public int doSomething2() {
		new SomeValueObject().doSomething();
		return 6;
	}

}
