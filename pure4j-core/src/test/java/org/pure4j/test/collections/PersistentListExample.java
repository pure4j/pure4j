package org.pure4j.test.collections;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.IPersistentList;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.ITransientCollection;
import org.pure4j.collections.PersistentList;
import org.pure4j.collections.PureCollections;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class PersistentListExample extends AbstractTest {
	
	@Pure
	@ShouldBePure
	public static int sumPersistentList(PersistentList<Integer> someInts) {
		int total = 0;
		for (Integer integer : someInts) {
			total += integer;
		}
		
		someInts.cons(55);
		
		return total;
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testBusinessLogic() {
		// test persistence
		PersistentList<Integer> pl = new PersistentList<Integer>(5);
		pl = pl.cons(10);
		int j = sumPersistentList(pl);
		assertEquals(15,j);
		assertEquals(2, pl.size());
		
		// test sorting
		assertEquals(new PersistentList<Integer>(10).cons(5), PureCollections.sort(pl));
		
		// test transient
		ITransientCollection<Integer> tran = pl.asTransient();
		tran.add(77);
		assertEquals("[10, 5, 77]", tran.persistent().toString());
	}
	
	@Test
	public void testConstruction() {
		// array
		IPersistentList<String> pv = new PersistentList<String>(someStrings(60));
		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
		
		// iseq
		pv = new PersistentList<String>((ISeq<String>) new ArraySeq<String>(someStrings(60)));
		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
		
		// list based
		pv = new PersistentList<String>(Arrays.asList(someStrings(60)));
		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
		 
		// no-args
		pv = new PersistentList<String>();
		pv = pv.addAll(new ArraySeq<String>(someStrings(60))); 
		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));

	}
	
	private String[] someStrings(int size) {
		String[] out = new String[size];
		for (int i = 0; i < out.length; i++) {
			out[i] = "str"+i;
		}
		
		return out;
	}
}
