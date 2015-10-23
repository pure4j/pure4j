package org.pure4j.test.collections;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.IterableSeq;
import org.pure4j.collections.PureCollections;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class IteratorSeqExample extends AbstractTest {

	@Pure
	@ShouldBePure
	public <K> void checkSeq(ISeq<K> in, int expectedLength, K expectedFirst) {
		Pure4J.immutable(expectedFirst);
		log("seq: "+in);
		
		assertEquals(expectedLength, in.size());
		assertEquals(expectedFirst, in.first());
	}
	
	
	@Test
	public void sanityTest() {
		List<String> items = new ArrayList<String>();
		items.add("first");
		items.add("second");
		items.add("third");
		
		// check persistence
		ISeq<String> seq = new IterableSeq<String>(items);
		checkSeq(seq, 3, "first");
		checkSeq(seq, 3, "first");
		checkSeq(seq.next(), 2, "second");
		checkSeq(seq, 3, "first");
		
		// check sorting
		List<String> items2 = new ArrayList<String>();
		items2.add("third");
		items2.add("first");
		items2.add("second");
		Assert.assertEquals(PureCollections.sort(new IterableSeq<String>(items2)), seq);
		
	}
}
