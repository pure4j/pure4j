package org.pure4j.checker.spec.mutable_unshared.construct3;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.checker.support.ShouldBePure;

@MutableUnshared
public abstract class AbstractMutableUnshared {

	private String in;
	
	@ShouldBePure
	public AbstractMutableUnshared(String in) {
		this.in = in;
	}
	
	@ShouldBePure
	public int somePureMethod1() {
		return in.hashCode();
	}

}
