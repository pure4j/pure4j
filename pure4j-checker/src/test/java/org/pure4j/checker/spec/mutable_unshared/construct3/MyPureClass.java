package org.pure4j.checker.spec.mutable_unshared.construct3;

import org.pure4j.checker.basic.support.ShouldBePure;


public class MyPureClass extends AbstractClass {

	@ShouldBePure
	public MyPureClass(String in) {
		super(in);
	}

	@ShouldBePure
	public int somePureMethod2(String in) {
		return new MyPureClass(in).somePureMethod1();
	}
}
