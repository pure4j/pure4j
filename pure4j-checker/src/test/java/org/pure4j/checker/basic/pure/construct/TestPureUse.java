package org.pure4j.checker.basic.pure.construct;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.pure.classes.SomePureClass;
import org.pure4j.checker.basic.support.ShouldBePure;

public class TestPureUse extends AbstractChecker {

	
	@ShouldBePure
	@Pure
	public int doSomething() {
		return new SomePureClass().somePureOperation(5);
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
