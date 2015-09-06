package org.pure4j.checker.collections;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.PersistentTreeMap;

public class PersistentTreeMapExample extends AbstractChecker {

	@Pure
	public void pureMethod(PersistentTreeMap<String, String> in, int expectedKeys, int expectedVals) {
		System.out.println("keys:");
		for (Iterator<String> iterator = in.keyIterator(); iterator.hasNext();) {
			String v = iterator.next();
			System.out.println(v);
		}

		assertEquals(expectedKeys, in.keySet().size());
		
		for (Iterator<Entry<String, String>> iterator = in.iterator(); iterator.hasNext();) {
			Entry<String, String> e = iterator.next();
			System.out.println(e.getKey()+" "+e.getValue());
		}
		
		System.out.println("vals:");
		Collection<String> values = in.values();
		assertEquals(expectedVals, values.size());
		for (String entry : values) {
			System.out.println(entry);
		}
		
		System.out.println(in);
		
		in.assoc("james", "bond");
	}
	
	

	@Test
	@Pure
	public void sanityTestOfMap() {
		PersistentTreeMap<String, String> phm = new PersistentTreeMap<String, String>();
		phm = phm.assoc("rob", "moffat");
		phm = phm.assoc("peter", "moffat");
		phm = phm.assoc("fiona", "pauli");
		pureMethod(phm, 3, 3);
		phm = phm.assoc("testy", "mctest");
		pureMethod(phm, 4, 4);
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 0);
	}
	
}
