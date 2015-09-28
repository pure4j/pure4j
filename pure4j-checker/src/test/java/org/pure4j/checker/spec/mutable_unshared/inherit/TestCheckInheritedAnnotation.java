package org.pure4j.checker.spec.mutable_unshared.inherit;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class TestCheckInheritedAnnotation extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 2);
	}
}
