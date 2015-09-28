package org.pure4j.checker.spec.pure.math;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.ShouldBePure;


public class PureMathsTests {
	
	@Pure
	@ShouldBePure
	public int consumeBlah(int x) {
		return Math.abs(x);
	}
	
}
