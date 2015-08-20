package org.pure4j.checker.immutable_composition;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class TestCheckImmutableValueComposition extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
