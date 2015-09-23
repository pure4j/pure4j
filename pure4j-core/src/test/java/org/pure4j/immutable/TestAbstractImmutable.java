package org.pure4j.immutable;

import org.junit.Assert;
import org.junit.Test;

public class TestAbstractImmutable {

	@Test
	public void testEquals() {
		Bob b1 = new Bob("hello", 43);
		Bob b2 = new Bob("hello", 43);
		Assert.assertEquals(b1, b1);
		Assert.assertEquals(b1.hashCode(), b2.hashCode());
		Assert.assertEquals("org.pure4j.immutable.Bob[hello,43]", b1.toString());
		Assert.assertEquals(0,  b1.compareTo(b2));
	}
	
	@Test
	public void testNotEquals() {
		Bob b1 = new Bob("hello", 41);
		Bob b2 = new Bob("hello", 43);
		Assert.assertFalse(b1.equals(b2));
		Assert.assertNotSame(b1.hashCode(), b2.hashCode());
		Assert.assertEquals(-1, b1.compareTo(b2));
		Assert.assertEquals(1, b2.compareTo(b1));
		
	}
	
	
}
