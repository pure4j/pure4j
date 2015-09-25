package org.pure4j.checker.spec.immutable.private_method;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class CheckHandlingOfProtectedAndPrivate extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1);
	}
	
}
