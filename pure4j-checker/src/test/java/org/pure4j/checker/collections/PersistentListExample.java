package org.pure4j.checker.collections;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.PersistentList;

public class PersistentListExample extends AbstractChecker {
	
	@Pure
	public int sumPersistentList(PersistentList<Integer> someInts) {
		int total = 0;
		for (Integer integer : someInts) {
			total += integer;
		}
		
		someInts.cons(55);
		
		return total;
	}
	
	@Test
	public void testBusinessLogic() {
		PersistentList<Integer> pl = new PersistentList<Integer>(5);
		pl = pl.cons(10);
		int j = sumPersistentList(pl);
		Assert.assertEquals(15,j);
		Assert.assertEquals(2, pl.size());
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 0);
	}
}
