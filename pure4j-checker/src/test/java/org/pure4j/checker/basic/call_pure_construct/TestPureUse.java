package org.pure4j.checker.basic.call_pure_construct;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.AbstractChecker;
import org.pure4j.checker.basic.pure_class.SomePureClass;

public class TestPureUse extends AbstractChecker {

	
	@Pure
	public int doSomething() {
		return new SomePureClass().somePureOperation(5);
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0,  1);
	}
}
