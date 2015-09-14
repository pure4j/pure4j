package org.pure4j.checker.basic.pure.construct2;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

@Pure
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
