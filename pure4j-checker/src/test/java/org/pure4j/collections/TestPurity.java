package org.pure4j.collections;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class TestPurity extends AbstractChecker {
 
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 2,  0);
	}
}
