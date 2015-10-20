package org.pure4j.test.checker.collections;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ITransientList;
import org.pure4j.collections.ITransientVector;
import org.pure4j.collections.TransientList;
import org.pure4j.collections.TransientQueue;
import org.pure4j.collections.TransientVector;
import org.pure4j.immutable.ClassNotImmutableException;
import org.pure4j.test.checker.Helper;
import org.pure4j.test.checker.support.ShouldBePure;

public class TransientsTest {

	@Test(expected=ClassNotImmutableException.class)
	public void smugglingTest1() {
		new TransientVector<Object>(new Object(), new Object());
	}
	
	@Test(expected=ClassNotImmutableException.class)
	public void smugglingTest2() {
		new TransientVector<Object>().add(new Object());
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testTransientVectorIterators() {
		ITransientVector<String> tv = new TransientVector<String>();
		tv.addAll(Arrays.asList("a", "b", "c"));
		tv.removeAll(Arrays.asList("b", "d"));
		tv.retainAll(Arrays.asList("a", "c"));
		Assert.assertEquals(Arrays.asList("a", "c"), tv);
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testTransientListIterators() {
		ITransientList<String> tv = new TransientList<String>();
		tv.addAll(Arrays.asList("a", "b", "c"));
		tv.removeAll(Arrays.asList("b", "d"));
		tv.retainAll(Arrays.asList("a", "c"));
		Assert.assertEquals(Arrays.asList("a", "c"), tv);
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testTransientQueueIterators() {
		TransientQueue<String> tq = new TransientQueue<String>();
		tq.add("baba");
		tq.add("ellie");
		tq.poll();
		Assert.assertEquals(Arrays.asList("ellie"), tq);
	}
	
	@Test 
	public void testPurity() {
		Helper.check(0, this.getClass());
	}
}
