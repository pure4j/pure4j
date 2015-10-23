package org.pure4j.test.collections;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.ITransientList;
import org.pure4j.collections.ITransientMap;
import org.pure4j.collections.ITransientVector;
import org.pure4j.collections.PersistentTreeMap;
import org.pure4j.collections.TransientHashMap;
import org.pure4j.collections.TransientHashSet;
import org.pure4j.collections.TransientList;
import org.pure4j.collections.TransientQueue;
import org.pure4j.collections.TransientTreeMap;
import org.pure4j.collections.TransientTreeSet;
import org.pure4j.collections.TransientVector;
import org.pure4j.immutable.ClassNotImmutableException;
import org.pure4j.test.ShouldBePure;

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
		
		Assert.assertEquals(tv, new TransientVector<String>("a", "c"));
		Assert.assertEquals(tv, new TransientVector<String>("a", "c"));
		Assert.assertEquals(tv, new TransientVector<String>(Arrays.asList("a", "c")));
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
		Assert.assertEquals(tv, new TransientList<String>("a", "c"));
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
		tq.offer("fred");
		Assert.assertEquals("ellie", tq.peek());
		Assert.assertEquals("ellie", tq.element());
		
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testTransientHashMap() {
		TransientHashMap<String, String> tm = new TransientHashMap<String, String>();
		tm.put("a", "aa");
		Assert.assertEquals("aa", tm.remove("a"));
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testTransientTreeMap() {
		TransientTreeMap<String, String> tm = new TransientTreeMap<String, String>();
		tm.put("a", "aa");
		tm.put("b", "bb");
		tm.put("c", "cc");
		Assert.assertEquals("aa", tm.remove("a"));
		Assert.assertEquals(new ArraySeq<String>("b", "c"), tm.keySeq());
		Assert.assertEquals(tm, new TransientTreeMap<String, String>((IPersistentMap<String, String>) tm.persistent()));
		Assert.assertEquals(tm, new TransientTreeMap<String, String>((Map<String, String>) tm));
		Assert.assertEquals(tm, new TransientTreeMap<String, String>(tm.persistent().seq()));
		
		Assert.assertEquals(tm, new TransientTreeMap<String, String>(PersistentTreeMap.DEFAULT_COMPARATOR, (IPersistentMap<String, String>) tm.persistent()));
		Assert.assertEquals(tm, new TransientTreeMap<String, String>(PersistentTreeMap.DEFAULT_COMPARATOR, (Map<String, String>) tm));
		Assert.assertEquals(tm, new TransientTreeMap<String, String>(PersistentTreeMap.DEFAULT_COMPARATOR, tm.persistent().seq()));
		

	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testTransientHashSet() {
		TransientHashSet<String> tm = new TransientHashSet<String>();
		tm.add("a");
		tm.add("b");
		tm.add("c");
		tm.add("d");
		Assert.assertEquals(true, tm.remove("a"));
		Assert.assertEquals(new ArraySeq<String>("b", "c", "d"), tm.seq());
		Assert.assertEquals(tm, new TransientHashSet<String>("d", "b", "c"));
		Assert.assertEquals(tm, new TransientHashSet<String>(Arrays.asList("d", "b", "c")));
		Assert.assertEquals(new TransientHashSet<String>(), new TransientHashSet<String>(100));
		Assert.assertEquals(new TransientHashSet<String>(), new TransientHashSet<String>(100, .5f));
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testTransientTreeSet() {
		TransientTreeSet<String> tm = new TransientTreeSet<String>();
		tm.add("a");
		tm.add("b");
		tm.add("c");
		tm.add("d");
		Assert.assertEquals(true, tm.remove("a"));
		Assert.assertEquals(new ArraySeq<String>("b", "c", "d"), tm.seq());
		Assert.assertEquals(tm, new TransientTreeSet<String>("d", "b", "c"));
		Assert.assertEquals(tm, new TransientTreeSet<String>(Arrays.asList("d", "b", "c")));
		Assert.assertEquals(tm, new TransientTreeSet<String>(new ArraySeq<String>("d", "b", "c")));
		Assert.assertEquals(tm, new TransientTreeSet<String>(tm));
		
		Assert.assertEquals(tm, new TransientTreeSet<String>(PersistentTreeMap.DEFAULT_COMPARATOR, "d", "b", "c"));
		Assert.assertEquals(tm, new TransientTreeSet<String>(PersistentTreeMap.DEFAULT_COMPARATOR, Arrays.asList("d", "b", "c")));
		Assert.assertEquals(tm, new TransientTreeSet<String>(PersistentTreeMap.DEFAULT_COMPARATOR, new ArraySeq<String>("d", "b", "c")));
		Assert.assertEquals(tm, new TransientTreeSet<String>(PersistentTreeMap.DEFAULT_COMPARATOR, tm));

	}

}
