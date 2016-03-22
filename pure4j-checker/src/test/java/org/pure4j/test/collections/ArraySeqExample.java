package org.pure4j.test.collections;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ASeq;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PureCollections;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class ArraySeqExample extends AbstractTest {

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
			assertEquals(0, ((List<K>)in).indexOf(expectedFirst));
		}
		
		assertEquals(0, in.lastIndexOf(expectedFirst));
		assertEquals(-1, in.lastIndexOf("sdkfhsk"));
		assertEquals(0, in.indexOf(expectedFirst));
		assertEquals(-1, in.indexOf("sjfhgds"));
		assertEquals(-1, in.indexOf(null));
		assertEquals(-1, in.lastIndexOf(null));
	}

	
	
	@Test
	public void testSanity() {
		
		// check persistence
		checkSeq(new ArraySeq<String>("A", "B", "C"), 3, "A");		// strings (i.e. objects)
		checkSeq(ArraySeq.create( 5, 6, 3 ), 3, 5);	// ints
		ISeq<Character> chars = ArraySeq.create('d', 'e', 'f', 'g' );	//chars
		checkSeq(chars, 4, 'd');
		checkSeq(chars.next(), 3, 'e');
		checkSeq(ArraySeq.create((short) 5, (short) 4, (short) 9), 3, (short) 5);  // short
		checkSeq(ArraySeq.create(5f, 4f, 9f), 3, 5f);  // float
		checkSeq(ArraySeq.create(5l, 4l, 9l), 3, 5l);  // long
		checkSeq(ArraySeq.create((byte) 5, (byte) 4, (byte) 9), 3, (byte) 5);  // long
		checkSeq(ArraySeq.create(5d, 4d, 9d ), 3, 5d);  // double
		checkSeq(new ArraySeq.BooleanSeq(true, false ), 2, true);  // boolean
		
		
		// check sorting
		Assert.assertEquals(PureCollections.sort(ArraySeq.create('d', 'e', 'g', 'f' )), ArraySeq.create('d', 'e', 'f', 'g'));
	}
	
	@Test(expected=RuntimeException.class)
	public void testRemove() {
		getInstance().remove("blah");
	}
	
	@Test(expected=RuntimeException.class)
	public void testClear() {
		getInstance().clear();
	}
	
	@Test(expected=RuntimeException.class)
	public void testAdd() {
		getInstance().add("blah");
	}
	
	@Test(expected=RuntimeException.class)
	public void testRetainAll() {
		getInstance().retainAll(null);
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddAll() {
		getInstance().addAll(Collections.emptyList());
	}
	
	@Test(expected=RuntimeException.class)
	public void testSet() {
		getInstance().set(3, "d");
	}
	

	private ASeq<String> getInstance() {
		return new ArraySeq<String>("bob");
	}



	@Test(expected=RuntimeException.class)
	public void testRemoveAll() {
		getInstance().removeAll(null);
	}
}

