package org.pure4j.checker.basic.runtime.checked;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;

public class CheckParameterImmutability extends AbstractChecker {

	private String s = "blah";
	
	/**
	 * Not going to be pure as we do something before checking
	 * @param in1
	 * @param in2
	 */
	@Pure
	public Object testParam1(Object in1, Object in2) {
		Object in3 = in2;
		Pure4J.immutable(in1, in2);
		return in3;
	}
	
	/**
	 * Should be fine.
	 */
	@Pure
	public Object testParam2(Object in1, Object in2) {
		Pure4J.immutable(in1, in2);
		return in1;
	}
	
	/**
	 * Should be fine.
	 */
	@Pure
	public Object testParam3(Object in1, int in2) {
		Pure4J.immutable(in1);
		return in1;
	}
	
	/**
	 * Not testing all the parameters
	 */
	@Pure
	public Object testParam4(Object in1, Object in2) {
		Pure4J.immutable(in1);
		return in1;
	}
	
	/**
	 *Calling with constants, shouldn't matter
	 */
	@Pure
	public Object testParam5(Object in1) {
		Pure4J.immutable(in1, 6);
		return in1;
	}
	
	/**
	 *Calling with members, shouldn't matter
	 */
	@Pure
	public Object testParam6(Object in1) {
		Pure4J.immutable(in1, s);
		return in1;
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0, 5);
	}
}
