package org.pure4j.test.checker.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ITransientMap;
import org.pure4j.collections.IterableSeq;
import org.pure4j.collections.PersistentTreeMap;
import org.pure4j.test.checker.support.AbstractChecker;
import org.pure4j.test.checker.support.ShouldBePure;

public class PersistentTreeMapExample extends AbstractChecker {

	@Pure
	@ShouldBePure
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
		// check persistence
		PersistentTreeMap<String, String> phm = new PersistentTreeMap<String, String>();
		phm = phm.assoc("rob", "moffat");
		phm = phm.assoc("peter", "moffat");
		phm = phm.assoc("fiona", "pauli");
		pureMethod(phm, 3, 3);
		phm = phm.assoc("testy", "mctest");
		pureMethod(phm, 4, 4);
		
		// check sorting (of keys)
		Assert.assertEquals(new ArraySeq<String>("fiona", "peter","rob", "testy"), new IterableSeq<String>(phm.keyIterator()));
		
		// check transient version
		ITransientMap<String, String> tm = phm.asTransient();
		tm.put("blah", "grommet");
		
		Assert.assertEquals(new ArraySeq<String>("blah", "fiona", "peter","rob", "testy"), new IterableSeq<String>(tm.persistent().keyIterator()));
		
	}
	
}
