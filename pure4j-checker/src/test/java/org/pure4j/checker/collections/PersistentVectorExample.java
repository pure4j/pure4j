package org.pure4j.checker.collections;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.AbstractChecker;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.collections.IPersistentVector;
import org.pure4j.collections.ITransientVector;
import org.pure4j.collections.PersistentVector;
import org.pure4j.collections.PureCollections;

public class PersistentVectorExample extends AbstractChecker {
	
	@Pure
	@ShouldBePure
	public int sumPersistentVector(PersistentVector<Integer> someInts) {
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
		PersistentVector<Integer> pl = PersistentVector.emptyVector();
		for (int i = 0; i <= 300; i++) {
			pl = pl.cons(i);
		}
		log(pl.toString());
		int j = sumPersistentVector(pl);
		Assert.assertEquals(150*301,j);
		Assert.assertEquals(301, pl.size());
		
		// test sorting
		PersistentVector<Integer> pv = PersistentVector.emptyVector();
		pv = pv.cons(6);
		pv = pv.cons(1);
		pv = pv.cons(2);
		pv = pv.cons(5);
		pv = pv.cons(4);
		pv = pv.cons(3);
		pv = pv.cons(0);
		Assert.assertEquals(PureCollections.sort(pv), pl.subList(0, 7));
		
		
		// test transient ver
		ITransientVector<Integer> trans = pv.asTransient();
		trans.add(64);
		IPersistentVector<Integer> toPers = trans.persistent();
		Assert.assertEquals("[6, 1, 2, 5, 4, 3, 0, 64]", toPers.toString());
		Assert.assertEquals("[6, 1, 2, 5, 4, 3, 0]", pv.toString());
		
		
	}

}
