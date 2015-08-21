package org.pure4j.checker.basic.library_case.client;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.AbstractChecker;
import org.pure4j.checker.basic.library_case.supplier.PureLibrary;

public class TestCanUsePureLibrary extends AbstractChecker {

	@Pure
	public int someFunction(String in) {
		return new PureLibrary().doSomething(in);
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0, 1);
	}
}
