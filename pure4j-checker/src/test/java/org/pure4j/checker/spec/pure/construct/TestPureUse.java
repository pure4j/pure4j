package org.pure4j.checker.spec.pure.construct;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.mutable_unshared.classes.SomeMutableUnsharedClass;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.checker.spec.immutable.calls_super.SomeValueObject;

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
