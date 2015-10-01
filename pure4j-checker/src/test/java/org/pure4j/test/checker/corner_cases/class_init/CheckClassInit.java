package org.pure4j.test.checker.corner_cases.class_init;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.test.checker.support.AbstractChecker;

public class CheckClassInit extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1);
	}
}
