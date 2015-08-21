package org.pure4j.checker.basic.pure_methods;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.basic.AbstractChecker;

public class TestCheckGoodPures extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0, 2);
	}
}
