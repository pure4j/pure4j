package org.pure4j.checker.collections;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.AbstractChecker;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.collections.IPersistentSet;
import org.pure4j.collections.ITransientSet;
import org.pure4j.collections.PersistentHashSet;

public class PersistentHashSetExample extends AbstractChecker {

	@Pure
	@ShouldBePure
	public void pureMethod(IPersistentSet<String> in, int expected) {
		log("keys:");
		for (String entry : in) {
			log(entry);
		}
		assertEquals(expected, in.size());
		
		in.cons("bobob");
	}
	
	@Test
	public void sanityTestOfSet() {
		
		// check persistence
		IPersistentSet<String> phm = PersistentHashSet.create("bob", "jeff", "gogo");
		phm = phm.cons("spencer");
		pureMethod(phm, 4);
		phm = phm.cons("spencera");
		phm = phm.cons("spencerb");
		phm = phm.cons("spencerc");
		phm = phm.cons("spencerc");		
		pureMethod(phm, 7);
		
		// check transient
		ITransientSet<String> set = phm.asTransient();
		set.add("something");
		set.remove("spencera");
		IPersistentSet<String> out = set.persistent();
		System.out.println(out);
	}
	
}
