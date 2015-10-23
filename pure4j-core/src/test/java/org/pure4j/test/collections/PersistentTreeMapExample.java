package org.pure4j.test.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ITransientMap;
import org.pure4j.collections.IterableSeq;
import org.pure4j.collections.PersistentTreeMap;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class PersistentTreeMapExample extends AbstractTest {

	@Pure
	@ShouldBePure
	public static void pureMethod(PersistentTreeMap<String, String> in, int expectedKeys, int expectedVals) {
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
	@ShouldBePure
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
		assertEquals(new ArraySeq<String>("fiona", "peter","rob", "testy"), new IterableSeq<String>(phm.keyIterator()));
		
		// check transient version
		ITransientMap<String, String> tm = phm.asTransient();
		tm.put("blah", "grommet");
		
		assertEquals(new ArraySeq<String>("blah", "fiona", "peter","rob", "testy"), new IterableSeq<String>(tm.persistent().keyIterator()));
		
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
	public void testConstruction() {
		// array
		int entries = 100;
		PersistentTreeMap<String, String> pm = new PersistentTreeMap<String, String>(makeMaps(entries));
		assertEquals(entries, pm.size());
		assertMapping(pm, entries); 
		System.out.println(pm);
		
		// iseq
		PersistentTreeMap<String, String> pm2 = new PersistentTreeMap<String, String>(pm.seq());
		assertEquals(pm2, pm);
//		
//		// map-based
		PersistentTreeMap<String, String> pm3 = new PersistentTreeMap<String, String>(pm);
		assertEquals(pm3, pm);
//		 
//		// no-args
		PersistentTreeMap<String, String> pm4 = new PersistentTreeMap<String, String>();
		for (Entry<String, String> entry : pm) {
			pm4 = pm4.assoc(entry.getKey(), entry.getValue());
		}
		assertEquals(pm, pm4);

	}
	


	private static void assertMapping(PersistentTreeMap<String, String> pm, int count) {
		String lastKey = null;
		for (Entry<String, String> entry : pm) {
			assertEquals(entry.getValue(),("k"+entry.getKey()));
			count --;
			if (lastKey != null) {
				int cmp = lastKey.compareTo(entry.getKey());
				assertEquals(-1, cmp);
			}
			lastKey = entry.getKey();
		}
		
		assertEquals(0, count);
	}

}
