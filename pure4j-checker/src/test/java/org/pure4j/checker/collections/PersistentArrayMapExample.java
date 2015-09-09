package org.pure4j.checker.collections;

import java.util.Collection;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.PersistentArrayMap;

public class PersistentArrayMapExample extends AbstractChecker {

	@Pure
	public void pureMethod(IPersistentMap<String, String> in, int expectedKeys, int expectedVals) {
		log("keys:");
//		Set<String> keySet = in.keySet();
//		assertEquals(expectedKeys, keySet.size());
		
		for (String entry : in.keySet()) {
			log(entry);
		}
		
		log("vals:");
		Collection<String> values = in.values();
		assertEquals(expectedVals, values.size());
		for (String entry : values) {
			log(entry);
		} 
		
		in.assoc("james", "bond");
	}
	
	

	@Test
	@Pure
	public void sanityTestOfMap() {
		IPersistentMap<String, String> phm = PersistentArrayMap.createWithCheck("a", "b");
		phm = phm.assoc("rob", "moffat");
		phm = phm.assoc("peter", "moffat");
		phm = phm.assoc("fiona", "pauli");
		pureMethod(phm, 4, 4);
		phm = phm.assoc("testy", "mctest");
		pureMethod(phm, 5, 5);
	}

	
}
