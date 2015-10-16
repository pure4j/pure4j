package org.pure4j.test.checker.collections;

import java.util.Collection;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.ITransientMap;
import org.pure4j.collections.PersistentArrayMap;
import org.pure4j.test.checker.Helper;
import org.pure4j.test.checker.support.AbstractChecker;
import org.pure4j.test.checker.support.ShouldBePure;

public class PersistentArrayMapExample extends AbstractChecker {

	@Pure
	@ShouldBePure
	public static void pureMethod(IPersistentMap<String, String> in, int expectedKeys, int expectedVals) {
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
	@ShouldBePure
	public void sanityTestOfMap() {
		// test persistence
		IPersistentMap<String, String> phm = new PersistentArrayMap<String, String>("a", "b");
		phm = phm.assoc("rob", "moffat");
		phm = phm.assoc("peter", "moffat");
		phm = phm.assoc("fiona", "pauli");
		pureMethod(phm, 4, 4);
		phm = phm.assoc("testy", "mctest");
		pureMethod(phm, 5, 5);
		
		// test transient version
		ITransientMap<String, String> trans = phm.asTransient();
		trans.put("named", "valued");
		assertEquals("{a=b, rob=moffat, testy=mctest, peter=moffat, named=valued, fiona=pauli}", trans.persistent().toString());
	}

	@Test
	public void testConstruction() {
		// array
		int entries = 100;
		PersistentArrayMap<String, String> pm = new PersistentArrayMap<String, String>(makeMaps(entries));
		assertEquals(entries, pm.size());
		assertMapping(pm, entries);
		System.out.println(pm);
		
		// iseq
		PersistentArrayMap<String, String> pm2 = new PersistentArrayMap<String, String>(pm.seq());
		assertEquals(pm2, pm);
//		 
//		// map-based
		PersistentArrayMap<String, String> pm3 = new PersistentArrayMap<String, String>(pm);
		assertEquals(pm3, pm);
//		  
//		// no-args
		IPersistentMap<String, String> pm4 = new PersistentArrayMap<String, String>();
		for (Entry<String, String> entry : pm) {
			pm4 = pm4.assoc(entry.getKey(), entry.getValue());
		}
		assertEquals(pm, pm4);

	}
	
	

	private void assertMapping(PersistentArrayMap<String, String> pm, int count) {
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
	public void testPurity() {
		Helper.check(0, this.getClass());
	}
}
