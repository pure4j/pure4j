package org.pure4j.test.checker.collections;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.ITransientMap;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.test.checker.Helper;
import org.pure4j.test.checker.support.AbstractChecker;
import org.pure4j.test.checker.support.ShouldBePure;

public class PersistentHashMapExample extends AbstractChecker {

	@Pure
	@ShouldBePure
	public static void pureMethod(IPersistentMap<String, String> in, int expectedKeys, int expectedVals) {
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
	public void testConstruction() {
		// array
		int entries = 100;
		PersistentHashMap<String, String> pm = new PersistentHashMap<String, String>(makeMaps(entries));
		assertEquals(entries, pm.size());
		assertMapping(pm, entries);
		System.out.println(pm);
		
		// iseq
//		pv = new PersistentList<String>((ISeq<String>) new ArraySeq<String>(someStrings(60)));
//		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
//		
//		// list based
//		pv = new PersistentList<String>(Arrays.asList(someStrings(60)));
//		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));
//		 
//		// no-args
//		pv = new PersistentList<String>();
//		pv = pv.addAll(new ArraySeq<String>(someStrings(60))); 
//		Assert.assertEquals(pv, Arrays.asList(someStrings(60)));

	}
	
	

	private void assertMapping(PersistentHashMap<String, String> pm, int count) {
		for (Entry<String, String> entry : pm) {
			assert(entry.getValue().equals("k"+entry.getKey()));
			count --;
		}
		
		assertEquals(0, count);
	}

	private String[] makeMaps(int i) {
		String[] out = new String[i*2];
		for (int j = 0; j < i; j++) {
			out[j*2] = "k"+j;
			out[j*2+1] = "kk"+j;
		}
		return out;
	}

	@Test
	@Pure
	@ShouldBePure
	public void sanityTestOfMap() {
		// test persistence
		PersistentHashMap<String, String> phm = PersistentHashMap.emptyMap();
		phm = phm.assoc("rob", "moffat");
		phm = phm.assoc("peter", "moffat");
		phm = phm.assoc("fiona", "pauli");
		pureMethod(phm, 3, 3);
		phm = phm.assoc("testy", "mctest");
		pureMethod(phm, 4, 4);
		
		
		// test transient version
		String toStringMap = phm.toString();
		ITransientMap<String, String> theTransVer = phm.asTransient();
		theTransVer.put("new", "Stuff");
		assertEquals(toStringMap, phm.toString());
		assertEquals(phm, theTransVer.persistent().without("new"));
	}
	
	@Test 
	public void testPurity() {
		Helper.check(0, this.getClass());
	}
}
