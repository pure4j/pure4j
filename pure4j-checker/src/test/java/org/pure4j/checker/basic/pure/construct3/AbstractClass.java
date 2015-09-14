package org.pure4j.checker.basic.pure.construct3;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;

@Pure
public abstract class AbstractClass {

	private String in;
	
	@ShouldBePure
	public AbstractClass(String in) {
		this.in = in;
	}
	
	@ShouldBePure
	public int somePureMethod1() {
		return in.hashCode();
	}

}
