package org.pure4j.checker.basic.pure.math;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.ShouldBePure;


public class PureMathsTests extends AbstractChecker {
	
	@Pure
	@ShouldBePure
	public int consumeBlah(int x) {
		return Math.abs(x);
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
