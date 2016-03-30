package org.pure4j.test.checker.cf;

import org.junit.Test;
import org.pure4j.test.checker.Helper;

public class TestCheckerFramework {

	@Test
	public void testImmutableUse() {
		Helper.check(1, ImmutableUse.class);
	}
	
	@Test
	public void testImmutableMisuse() {
		Helper.check(1, ImmutableMisuse.class);
	}
}
