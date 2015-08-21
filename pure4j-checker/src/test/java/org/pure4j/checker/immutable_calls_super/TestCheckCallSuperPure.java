package org.pure4j.checker.immutable_calls_super;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class TestCheckCallSuperPure extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 6);
	}
}
