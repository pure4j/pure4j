package org.pure4j.checker.basic.immutable.missing_final;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.exception.ClassExpectingPureMethod;

public class TestCheckWellMadeImmutable extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0, ClassExpectingPureMethod.class // missing toString
				);
	}
}
