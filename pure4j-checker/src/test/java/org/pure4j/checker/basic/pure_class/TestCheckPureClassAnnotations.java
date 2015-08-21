package org.pure4j.checker.basic.pure_class;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.basic.AbstractChecker;

public class TestCheckPureClassAnnotations extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 2, 5);
	}
}
