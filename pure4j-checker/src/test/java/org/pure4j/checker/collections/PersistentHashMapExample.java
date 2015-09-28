package org.pure4j.checker.collections;

import java.util.Collection;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.AbstractChecker;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.ITransientMap;
import org.pure4j.collections.PersistentHashMap;

public class PersistentHashMapExample extends AbstractChecker {

	@Pure
	@ShouldBePure
	public void pureMethod(IPersistentMap<String, String> in, int expectedKeys, int expectedVals) {
		log("keys:");
		Set<String> keySet = in.keySet();
		assertEquals(expectedKeys, keySet.size());
		
		for (String entry : keySet) {
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
		PersistentHashMap<String, String> phm = PersistentHashMap.emptyMap();
		phm = phm.assoc("rob", "moffat");
		phm = phm.assoc("peter", "moffat");
		phm = phm.assoc("fiona", "pauli");
		pureMethod(phm, 3, 3);
		phm = phm.assoc("testy", "mctest");
		pureMethod(phm, 4, 4);
		
		String toStringMap = phm.toString();
		ITransientMap<String, String> theTransVer = phm.asTransient();
		theTransVer = theTransVer.assoc("new", "Stuff");
		Assert.assertEquals(toStringMap, phm.toString());
		Assert.assertEquals(phm, theTransVer.persistent().without("new"));
	}
	
}
