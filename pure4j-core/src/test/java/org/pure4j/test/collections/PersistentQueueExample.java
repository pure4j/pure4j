package org.pure4j.test.collections;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ITransientQueue;
import org.pure4j.collections.PersistentQueue;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class PersistentQueueExample extends AbstractTest {

	@Pure
	@ShouldBePure
	public static void checkIt(PersistentQueue<String> in, int expectedSize, String expectedFirst) {
		Pure4J.immutable(expectedFirst);
		log("queue:"+in.toString());
		assertEquals(expectedSize, in.size());
		
		in.cons("james");
		assertEquals(expectedFirst, in.peek());
	}
	
	

	@Test
	@Pure
	@ShouldBePure
	public void sanityTestOfQueue() {
		
		// check persistence
		PersistentQueue<String> q = PersistentQueue.emptyQueue();
		q = q.cons("first");
		q = q.cons("second");
		q = q.cons("third");
		checkIt(q, 3, "first");
		q = q.pop();
		
		q= q.cons("chicken");
		q = q.cons("basket");
		checkIt(q, 4, "second");
		
		// check transient
		ITransientQueue<String> tq = q.asTransient();
		tq.add("something");
		tq.add("else");
		
		assertEquals("[second, third, chicken, basket, something, else]", tq.persistent().toString());
		
		log(tq.toString());
		
	}

	
}
