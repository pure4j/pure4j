package org.pure4j.checker.collections;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.AbstractChecker;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.collections.PersistentQueue;

public class PersistentQueueExample extends AbstractChecker {

	@Pure
	@ShouldBePure
	public void checkIt(PersistentQueue<String> in, int expectedSize, String expectedFirst) {
		log("queue:"+in.toString());
		assertEquals(expectedSize, in.size());
		
		in.cons("james");
		assertEquals(expectedFirst, in.peek());
	}
	
	

	@Test
	@Pure
	public void sanityTestOfQueue() {
		PersistentQueue<String> q = PersistentQueue.emptyQueue();
		q = q.cons("first");
		q = q.cons("second");
		q = q.cons("third");
		checkIt(q, 3, "first");
		q = q.pop();
		
		q= q.cons("chicken");
		q = q.cons("basket");
		checkIt(q, 4, "second");
	}

	
}
