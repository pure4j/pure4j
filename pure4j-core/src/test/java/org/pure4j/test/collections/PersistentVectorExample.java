package org.pure4j.test.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.IPersistentCollection;
import org.pure4j.collections.IPersistentVector;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.ITransientVector;
import org.pure4j.collections.PersistentVector;
import org.pure4j.collections.PureCollections;
import org.pure4j.test.ShouldBePure;

public class PersistentVectorExample extends AbstractCollectionTest {
	
	@Pure
	@ShouldBePure
	public static int sumPersistentVector(PersistentVector<Integer> someInts) {
		int total = 0;
		for (Integer integer : someInts) {
			total += integer;
		}
		
		someInts.cons(55);
		
		return total;
	}
	
	@Pure
	@ShouldBePure
	public IPersistentCollection<String> getInstance() {
		return new PersistentVector<String>();
	}
	
	@Test
	@Pure
	@ShouldBePure
	public void testBusinessLogic2() {
		PersistentVector<Integer> pl = new PersistentVector<Integer>(Arrays.asList(0,1,2,3,4,5));
		pl = pl.assocN(0, 10);
		pl = pl.assocN(6, 6);
		assertEquals(10, pl.get(0));
		assertEquals(6, pl.get(6));
		assertEquals(7, pl.size());
		assertEquals(3, pl.nth(3));
		assertEquals(20, pl.nth(10, 20));
		
		
		
	}
	
	
	@Test
	@Pure
	@ShouldBePure
	public void testBusinessLogic() {
		// test persistence
		PersistentVector<Integer> pl = PersistentVector.emptyVector();
		for (int i = 0; i <= 300; i++) {
			pl = pl.cons(i);
		}
		log(pl.toString());
		int j = sumPersistentVector(pl);
		assertEquals(150*301,j);
		assertEquals(301, pl.size());
		
		// test sorting
		PersistentVector<Integer> pv = PersistentVector.emptyVector();
		pv = pv.cons(6);
		pv = pv.cons(1);
		pv = pv.cons(2);
		pv = pv.cons(5);
		pv = pv.cons(4);
		pv = pv.cons(3);
		pv = pv.cons(0);
		assertEquals(PureCollections.sort(pv), pl.subList(0, 7));
		
		
		// test transient ver
		ITransientVector<Integer> trans = pv.asTransient();
		trans.add(64);
		IPersistentVector<Integer> toPers = trans.persistent();
		assertEquals("[6, 1, 2, 5, 4, 3, 0, 64]", toPers.toString());
		assertEquals("[6, 1, 2, 5, 4, 3, 0]", pv.toString());
		
		PersistentVector<Integer> pint = new PersistentVector<Integer>(5, 6, 3, 1, 5);
		int sum = 0;
		for (Integer integer : pint.subList(2, 4)) {
			sum += integer;
		}
		assertEquals(4, sum);
	}
	
	@Test
	public void testConstruction() {
		// array
		IPersistentVector<String> pv = new PersistentVector<String>(someStrings(60));
		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
		
		// iseq
		pv = new PersistentVector<String>((ISeq<String>) new ArraySeq<String>(someStrings(60)));
		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
		
		// list based
		pv = new PersistentVector<String>(Arrays.asList(someStrings(60)));
		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
		 
		// no-args
		pv = new PersistentVector<String>();
		pv = pv.addAll(new ArraySeq<String>(someStrings(60))); 
		List<String> elems = Arrays.asList(someStrings(60));
		Assert.assertEquals(pv, elems);

	}

	private String[] someStrings(int size) {
		String[] out = new String[size];
		for (int i = 0; i < out.length; i++) {
			out[i] = "str"+i;
		}
		
		return out;
	}
}
