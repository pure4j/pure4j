package org.pure4j.checker.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.IPersistentSet;
import org.pure4j.collections.PersistentTreeSet;

public class PersistentTreeSetExample extends AbstractChecker {

	@Pure
	@ShouldBePure
	public void pureMethod(IPersistentSet<String> in, int expected) {
		log("keys:"+in);
		
		List<String> l1 = new ArrayList<String>(in);
		Collections.sort(l1);
		String inStr = in.seq().toString();
		String l1Str = l1.toString();
		assertEquals(l1Str, inStr);
		
		in.cons("bobob");
	}
	
	@Test
	public void sanityTestOfSet() {
		
		IPersistentSet<String> phm1 = PersistentTreeSet.create("bob", "jeff", "gogo", "zzap");
		IPersistentSet<String> phm = phm1.cons("spencer");
		pureMethod(phm, 4);
		phm = phm.cons("spencera");
		phm = phm.cons("spencerb");
		phm = phm.cons("spencerc");
		phm = phm.cons("spencerc");
		pureMethod(phm, 7);
		
		Assert.assertEquals(phm1, PersistentTreeSet.create("bob", "gogo", "jeff", "zzap"));
	}
	
}
