package org.pure4j.checker.basic.library.case1.client;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.library.case1.supplier.PureLibrary;
import org.pure4j.checker.basic.support.ShouldBePure;

public class TestCanUsePureLibrary extends AbstractChecker {

	@Pure
	@ShouldBePure
	public int someFunction(String in) {
		return new PureLibrary().doSomething(in);
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
