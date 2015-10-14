package org.pure4j.test.checker.collections;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PureCollections;
import org.pure4j.test.checker.Helper;
import org.pure4j.test.checker.support.AbstractChecker;
import org.pure4j.test.checker.support.ShouldBePure;

public class ArraySeqExample extends AbstractChecker {

	@SuppressWarnings("unchecked")
	@Pure
	@ShouldBePure
	public static <K>  void checkSeq(ISeq<K> in, int expectedLength, K expectedFirst) {
		Pure4J.immutable(in, expectedFirst);
		
		log("seq: "+in.toString());
		
		log(in.next().toString());
		
		assertEquals(expectedLength, in.size());
		assertEquals(expectedFirst, in.first());
		if (in instanceof List) {
			
		}
		assertEquals(0, ((List<K>)in).indexOf(expectedFirst));
	}

	
	
	@Test
	public void testSanity() {
		
		// check persistence
		checkSeq(new ArraySeq<String>("A", "B", "C"), 3, "A");		// strings (i.e. objects)
		checkSeq(ArraySeq.create( 5, 5, 3 ), 3, 5);	// ints
		ISeq<Character> chars = ArraySeq.create('d', 'e', 'f', 'g' );	//chars
		checkSeq(chars, 4, 'd');
		checkSeq(chars.next(), 3, 'e');
		
		checkSeq(ArraySeq.create(5f, 4f, 9f), 3, 5f);  // float
		checkSeq(ArraySeq.create(5d, 4d, 9d ), 3, 5d);  // double
		checkSeq(new ArraySeq.BooleanSeq(true, false ), 2, true);  // boolean
		
		
		// check sorting
		Assert.assertEquals(PureCollections.sort(ArraySeq.create('d', 'e', 'g', 'f' )), ArraySeq.create('d', 'e', 'f', 'g'));
	}
	
	@Test 
	public void testPurity() {
		Helper.check(0, this.getClass());
	}
}

