package org.pure4j.checker.collections;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.AbstractChecker;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.EnumerationSeq;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PureCollections;

public class EnumerationSeqExample extends AbstractChecker {

	@Pure
	@ShouldBePure
	public <K> void checkSeq(ISeq<K> in, int expectedLength, K expectedFirst) {
		
		assertEquals(expectedLength, in.count());
		assertEquals(expectedFirst, in.first());
	}
	
	
	@Test
	public void sanityTest() {
		Vector<String> items = new Vector<String>();
		items.add("first");
		items.add("second");
		items.add("third");
		
		
		// check out seq persistence
		Enumeration<String> elements = items.elements();
		ISeq<String> seq = new EnumerationSeq<String>(elements);
		checkSeq(seq, 3, "first");
		checkSeq(seq, 3, "first");
		checkSeq(seq.next(), 2, "second");
		checkSeq(seq, 3, "first");
		checkSeq(seq.next().next(), 1, "third");
		
		// check out sorting
		Collections.shuffle(items);
		Assert.assertEquals(
				PureCollections.sort(new EnumerationSeq<String>(items.elements())),
				new ArraySeq<>("first", "second", "third")
				);
		
		log("seq: "+seq);		

	}
}
