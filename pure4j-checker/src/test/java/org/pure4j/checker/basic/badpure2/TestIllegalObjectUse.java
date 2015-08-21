package org.pure4j.checker.basic.badpure2;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.AbstractChecker;

public class TestIllegalObjectUse extends AbstractChecker {

	
	@Pure
	public int doSomething() {
		return new StringBuffer().hashCode();
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 2,  0);
	}
}
