package org.pure4j.checker.badpure2;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.processor.Callback;

public class TestIllegalObjectUse extends AbstractChecker {

	
	@Pure
	public int doSomething() {
		return new StringBuffer().hashCode();
	}
	
	@Pure
	public Object doSomethingElse() {
		return this;
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 3);
	}
}
