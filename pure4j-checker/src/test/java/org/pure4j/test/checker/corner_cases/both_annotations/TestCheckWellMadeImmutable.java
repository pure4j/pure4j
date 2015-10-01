package org.pure4j.test.checker.corner_cases.both_annotations;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.test.checker.support.AbstractChecker;

public class TestCheckWellMadeImmutable extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
