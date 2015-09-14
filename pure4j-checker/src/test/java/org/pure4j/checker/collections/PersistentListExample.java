package org.pure4j.checker.collections;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.collections.PersistentList;

public class PersistentListExample extends AbstractChecker {
	
	@Pure
	@ShouldBePure
	public int sumPersistentList(PersistentList<Integer> someInts) {
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
		PersistentList<Integer> pl = new PersistentList<Integer>(5);
		pl = pl.cons(10);
		int j = sumPersistentList(pl);
		assertEquals(15,j);
		assertEquals(2, pl.size());
	}
}
