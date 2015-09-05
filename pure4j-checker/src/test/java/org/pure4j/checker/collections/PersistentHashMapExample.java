package org.pure4j.checker.collections;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.PersistentHashMap;

public class PersistentHashMapExample extends AbstractChecker {

	@Pure
	public void pureMethod(IPersistentMap<String, String> in) {
		System.out.println("keys:");
		for (String entry : in.keySet()) {
			System.out.println(entry);
		}
		
		System.out.println("vals:");
		for (String entry : in.values()) {
			System.out.println(entry);
		}
		
		in.assoc("james", "bond");
	}
	
	@Test
	public void sanityTestOfMap() {
		
		IPersistentMap<String, String> phm = PersistentHashMap.emptyMap();
		phm = phm.assoc("rob", "moffat");
		phm = phm.assoc("peter", "moffat");
		phm = phm.assoc("fiona", "pauli");
		pureMethod(phm);
		phm = phm.assoc("testy", "mctest");
		pureMethod(phm);
		
		
	}
	
}
