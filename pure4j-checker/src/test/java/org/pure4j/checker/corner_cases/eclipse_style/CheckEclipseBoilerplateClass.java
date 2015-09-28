package org.pure4j.checker.corner_cases.eclipse_style;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.support.AbstractChecker;

public class CheckEclipseBoilerplateClass extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
