package org.pure4j.checker.spec.mutable_unshared.construct3;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.basic.support.ShouldBePure;

@MutableUnshared
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
