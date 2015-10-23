package org.pure4j.test.checker.spec.mutable_unshared.construct3;

import org.pure4j.test.ShouldBePure;


public class MyMutableUnsharedClass extends AbstractMutableUnshared {

	@ShouldBePure
	public MyMutableUnsharedClass(String in) {
		super(in);
	}

	@ShouldBePure
	public int somePureMethod2(String in) {
		return new MyMutableUnsharedClass(in).somePureMethod1();
	}
}
