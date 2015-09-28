package org.pure4j.checker.spec.mutable_unshared.construct2;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.basic.support.ShouldBePure;

@MutableUnshared
public class MyPureClass {

	@Override
	@ShouldBePure
	public int hashCode() {
		return 55;
	}

	@ShouldBePure
	public int somePureMethod() {
		return new MyPureClass().hashCode();
	}
}
