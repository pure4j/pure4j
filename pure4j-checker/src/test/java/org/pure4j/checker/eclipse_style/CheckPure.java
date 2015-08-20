package org.pure4j.checker.eclipse_style;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class CheckPure extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
