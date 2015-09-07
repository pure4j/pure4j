package org.pure4j.checker.collections;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;

public class ArraySeqExample extends AbstractChecker{

	@Pure
	public <K> void checkSeq(ISeq<K> in, int expectedLength, K expectedFirst) {
		System.out.println("seq: "+in);
		
		assertEquals(expectedLength, in.count());
		assertEquals(expectedFirst, in.first());
	}
	
	@Test
	public void testSanity() {
		checkSeq(ArraySeq.create("A", "B", "C"), 3, "A");
		checkSeq(ArraySeq.create(new int[] { 5, 5, 3 }), 3, 5);
		
		ISeq<Character> chars = ArraySeq.create(new char[] { 'd', 'e', 'f', 'g' });
		checkSeq(chars, 4, 'd');
		checkSeq(chars.next(), 3, 'e');
		
	}
}

