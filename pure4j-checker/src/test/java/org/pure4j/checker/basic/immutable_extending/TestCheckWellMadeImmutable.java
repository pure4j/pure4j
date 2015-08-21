package org.pure4j.checker.basic.immutable_extending;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.basic.AbstractChecker;

public class TestCheckWellMadeImmutable extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0, 5);
	}
}
