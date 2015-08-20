package org.pure4j.checker.badpure1;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.processor.Callback;

public class TestCheckCatchesNonImmutableValueArguments extends AbstractChecker {

	@Pure
	public int someFunction(InputStream is) {
		return 0;
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 0);
	}
}
