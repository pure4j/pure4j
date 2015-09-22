package org.pure4j.checker.basic.mutable_unshared.return_this;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class TestPureUse2 extends AbstractChecker {
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
