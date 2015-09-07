package org.pure4j.checker.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.IPersistentSet;
import org.pure4j.collections.PersistentTreeSet;

public class PersistentTreeSetExample extends AbstractChecker {

	@Pure
	public void pureMethod(IPersistentSet<String> in, int expected) {
		System.out.println("keys:"+in);
		
		List<String> l1 = new ArrayList<String>(in);
		Collections.sort(l1);
		String inStr = in.seq().toString();
		String l1Str = l1.toString();
		assertEquals(l1Str, inStr);
		
		in.cons("bobob");
	}
	
	@Test
	public void sanityTestOfSet() {
		
		IPersistentSet<String> phm = PersistentTreeSet.create("bob", "jeff", "gogo", "zzap");
		phm = phm.cons("spencer");
		pureMethod(phm, 4);
		phm = phm.cons("spencera");
		phm = phm.cons("spencerb");
		phm = phm.cons("spencerc");
		phm = phm.cons("spencerc");		
		pureMethod(phm, 7);
	}
	
}
