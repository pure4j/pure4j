package org.pure4j.checker.basic.immutable.no_to_string;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.exception.ClassExpectingPureMethod;

public class TestCheckNotImplementingToString extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, ClassExpectingPureMethod.class);
	}
}
