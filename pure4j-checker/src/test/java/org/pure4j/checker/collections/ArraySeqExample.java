package org.pure4j.checker.collections;

import java.util.List;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;

public class ArraySeqExample extends AbstractChecker{

	@SuppressWarnings("unchecked")
	@Pure
	@ShouldBePure
	public <K> void checkSeq(ISeq<K> in, int expectedLength, K expectedFirst) {
		Pure4J.immutable(in, expectedFirst);
		
		log("seq: "+in.toString());
		
		log(in.iterator().next().toString());
		
		assertEquals(expectedLength, in.count());
		assertEquals(expectedFirst, in.first());
		if (in instanceof List) {
			
		}
		assertEquals(0, ((List<K>)in).indexOf(expectedFirst));
	}

	
	
	@Test
	public void testSanity() {
		checkSeq(ArraySeq.create("A", "B", "C"), 3, "A");		// strings (i.e. objects)
		checkSeq(ArraySeq.create(new int[] { 5, 5, 3 }), 3, 5);	// ints
		ISeq<Character> chars = ArraySeq.create(new char[] { 'd', 'e', 'f', 'g' });	//chars
		checkSeq(chars, 4, 'd');
		checkSeq(chars.next(), 3, 'e');
		
		checkSeq(ArraySeq.create(new float[] {5f, 4f, 9f }), 3, 5f);  // float
		checkSeq(ArraySeq.create(new double[] {5d, 4d, 9d }), 3, 5d);  // double
		checkSeq(ArraySeq.create(new boolean[] {true, false }), 2, true);  // boolean
		
		
	}
}

