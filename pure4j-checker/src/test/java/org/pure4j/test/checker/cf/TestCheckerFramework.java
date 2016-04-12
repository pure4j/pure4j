package org.pure4j.test.checker.cf;

import org.junit.Test;
import org.pure4j.test.checker.Helper;

/**
 * These are some tests of things that we would expect to work if we have used the checker framework
 * annotations correctly.
 * 
 * @author robmoffat
 *
 */
public class TestCheckerFramework {

	@Test
	public void testImmutableUse() {
		Helper.check(1, ImmutableUse.class);
	}
	
	@Test
	public void testImmutableMisuse() {
		Helper.check(1, ImmutableMisuse.class);
	}
	
	@Test
	public void testPolyImmutableUse() {
		Helper.check(1, PolyImmutableUse.class);
	}
	
	@Test
	public void testPolyImmutableMisuse() {
		Helper.check(1, PolyImmutableMisuse.class);
	}
	
	@Test
	public void testImmutableCallUse() {
		Helper.check(1, ImmutableCallUse.class);
	}
}
