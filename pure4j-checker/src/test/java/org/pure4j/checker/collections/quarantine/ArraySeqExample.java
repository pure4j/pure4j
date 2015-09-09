package org.pure4j.checker.collections.quarantine;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;

public class ArraySeqExample extends AbstractChecker{

	@Pure
	public <K> void checkSeq(ISeq<String> in, int expectedLength, String expectedFirst) {
		
		log("seq: "+in.toString());
		
		log(in.iterator().next());
		
		assertEquals(expectedLength, in.count());
		assertEquals(expectedFirst, in.first());
	}
	
	@Pure
	public <K> void checkSeq(ISeq<Character> in, int expectedLength, char expectedFirst) {
		
		log("seq: "+in.toString());
		
		log(""+in.iterator().next());
		
		assertEquals(expectedLength, in.count());
		assertEquals(expectedFirst, in.first());
	}
	
	@Pure
	public <K> void checkSeq(ISeq<Integer> in, int expectedLength, int expectedFirst) {
		
		log("seq: "+in.toString());
		
		log(""+in.iterator().next());
		
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

