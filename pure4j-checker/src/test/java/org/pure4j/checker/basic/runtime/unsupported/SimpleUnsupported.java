package org.pure4j.checker.basic.runtime.unsupported;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.ShouldBePure;

public class SimpleUnsupported extends AbstractChecker {

	@Pure
	@ShouldBePure
	public void doSomething(Object in1, Object in2) {
		Pure4J.unsupported();
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
	
}
