package org.pure4j.checker.basic.immutable.equals;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class CheckEqualsImplementingOnImmutables extends AbstractChecker {

	/**
	 * Note: immutables no longer need to implement equals.  But they should.
	 * So, errors = 0 now.
	 * @throws IOException
	 */
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0, 5);
	}
}
