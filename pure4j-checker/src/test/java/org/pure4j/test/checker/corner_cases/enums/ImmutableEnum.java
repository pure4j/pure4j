package org.pure4j.test.checker.corner_cases.enums;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.test.ShouldBePure;
import org.pure4j.test.checker.support.AbstractChecker;

public class ImmutableEnum extends AbstractChecker {

	@ImmutableValue
	static enum Blah { A, B }
	
	@ShouldBePure
	@Pure
	public static Blah consumeBlah(Blah b) {
		return Blah.A;
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 2);
	}
}
