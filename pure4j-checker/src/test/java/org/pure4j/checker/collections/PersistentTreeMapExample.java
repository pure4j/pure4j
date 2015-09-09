package org.pure4j.checker.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.collections.PersistentTreeMap;

public class PersistentTreeMapExample extends AbstractChecker {

	@Pure
	public void pureMethod(PersistentTreeMap<String, String> in, int expectedKeys, int expectedVals) {
		log("keys:");
		for (Iterator<String> iterator = in.keyIterator(); iterator.hasNext();) {
			String v = iterator.next();
			log(v);
		}

		assertEquals(expectedKeys, in.keySet().size());
		
		for (Iterator<Entry<String, String>> iterator = in.iterator(); iterator.hasNext();) {
			Entry<String, String> e = iterator.next();
			log(e.getKey()+" "+e.getValue());
		}
		
		log("vals:");
		Collection<String> values = in.values();
		assertEquals(expectedVals, values.size());
		for (String entry : values) {
			log(entry);
		}
		
		log(in.toString());
		
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
	
}
