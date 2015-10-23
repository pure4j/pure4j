package org.pure4j.test.checker.spec.pure.math;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.ShouldBePure;


public class PureMathsTests {
	
	@Pure
	@ShouldBePure
	public int consumeBlah(int x) {
		return Math.abs(x);
	}
	
}
