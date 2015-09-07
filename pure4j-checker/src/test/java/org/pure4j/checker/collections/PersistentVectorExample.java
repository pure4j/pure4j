package org.pure4j.checker.collections;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.PersistentVector;

public class PersistentVectorExample extends AbstractChecker {
	
	@Pure
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
	public void testBusinessLogic() {
		PersistentVector<Integer> pl = PersistentVector.emptyVector();
		for (int i = 0; i <= 300; i++) {
			pl = pl.cons(i);
		}
		System.out.println(pl);
		int j = sumPersistentVector(pl);
		Assert.assertEquals(150*301,j);
		Assert.assertEquals(301, pl.size());
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 0);
	}
}
