package org.pure4j.checker.basic.corners.exception;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.ShouldBePure;

public class PureWithException extends AbstractChecker {

	@Pure
	@ShouldBePure
	public static int doSomething(int i) {
		try {
			return new Integer(i).toString().hashCode();
		} catch (RuntimeException re) {
			throw new UnsupportedOperationException("bob");
		}
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}

