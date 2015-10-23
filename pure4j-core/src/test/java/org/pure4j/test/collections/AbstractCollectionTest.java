package org.pure4j.test.collections;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.IPersistentCollection;
import org.pure4j.collections.IPersistentList;
import org.pure4j.collections.IPersistentStack;
import org.pure4j.collections.PersistentHashSet;
import org.pure4j.collections.PersistentList;
import org.pure4j.test.AbstractTest;

public abstract class AbstractCollectionTest extends AbstractTest {

	public abstract IPersistentCollection<String> getInstance();
	
	public boolean isLifo() {
		return true;
	}
	
	@Test
	public void testIterator() {
		getInstance().cons("g").cons("b");
		
		
	}
	
	
	@Test
	public void soakTestCollection() {
		IPersistentCollection<String> pc = getInstance();
		int count = 15;
		if (pc instanceof IPersistentStack) {
			IPersistentStack<String> stack = (IPersistentStack<String>) pc;
			
			for (int i = 0; i < count; i++) {
				stack = stack.cons("stackitem"+i);
			}
			
			ArraySeq<String> arrayseq = new ArraySeq<String>("a", "b", "c");
			stack = stack.addAll(arrayseq);
			assertEquals(isLifo() ? "c" : "stackitem0", stack.peek());
			
			assertEquals(false, stack.isEmpty());
			assertEquals(true, stack.empty().isEmpty());
			if (isLifo()) {
				stack = stack.empty().cons("c").cons("b").cons("a");
			} else {
				stack = stack.empty().cons("a").cons("b").cons("c");
			}
			assertEquals(arrayseq, new ArraySeq<String>((String[]) stack.toArray(new String[] {})));
			assertEquals(arrayseq, new ArraySeq<Object>((Object[]) stack.toArray()));
		}
		
		if (pc instanceof List) {
			@SuppressWarnings("unchecked")
			ListIterator<String> li = ((List<String>) pc).listIterator();
			while (li.hasNext()) {
				li.next();
			}
		}
		
		if (pc instanceof Iterable) {
			Iterator<String> i = pc.iterator();
			while (i.hasNext()) {
				i.next();
			}
		}
		
		if (pc instanceof IPersistentList) {
			IPersistentList<String> pl = (IPersistentList<String>) pc;
//			pl.
		}
	
		
		pc = pc.cons("something").cons("else");
		pc = pc.addAll(new ArraySeq<String>("a", "b"));
		assertEquals(true, pc.contains("something"));
		assertEquals(true, pc.containsAll(new PersistentList<String>("something","else")));
		assertEquals(false, pc.containsAll(new PersistentList<String>("something","happened")));
		assertEquals(pc, pc.asTransient().persistent());
		assertEquals(0, pc.empty().size());
		
		pc = pc.empty().cons("3").cons("2").cons("1");
		PersistentHashSet<String> pc2 = new PersistentHashSet<String>();
		for (String string : pc) {
			pc2 = pc2.cons(string);
		}
		
		assertEquals(pc2, new PersistentHashSet<String>(pc));
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
	public void testRemoveAll() {
		getInstance().removeAll(null);
	}

}
