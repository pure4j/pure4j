package org.pure4j.checker.spec.mutable_unshared.construct3;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class TestPureUse3 extends AbstractChecker {
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
