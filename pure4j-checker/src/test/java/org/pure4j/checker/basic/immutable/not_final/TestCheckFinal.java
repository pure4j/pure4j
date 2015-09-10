package org.pure4j.checker.basic.immutable.not_final;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

@Ignore("Have made final checking an option and currently it is disabled")
public class TestCheckFinal extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 3);
	}
}
