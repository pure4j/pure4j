package org.pure4j.checker.collections;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.IterableSeq;

public class IteratorSeqExample extends AbstractChecker {

	@Pure
	public <K> void checkSeq(ISeq<K> in, int expectedLength, K expectedFirst) {
		log("seq: "+in);
		
		assertEquals(expectedLength, in.count());
		assertEquals(expectedFirst, in.first());
	}
	
	
	@Test
	public void sanityTest() {
		List<String> items = new ArrayList<String>();
		items.add("first");
		items.add("second");
		items.add("third");
		ISeq<String> seq = new IterableSeq<String>(items);
		checkSeq(seq, 3, "first");
		checkSeq(seq, 3, "first");
		checkSeq(seq.next(), 2, "second");
		checkSeq(seq, 3, "first");
		
		
		
		
	}
}
