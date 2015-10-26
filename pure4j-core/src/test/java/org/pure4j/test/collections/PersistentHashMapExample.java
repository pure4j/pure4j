package org.pure4j.test.collections;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.ITransientMap;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.test.ShouldBePure;

public class PersistentHashMapExample extends AbstractMapTest {

	@Test
	@Pure
	@ShouldBePure
	public void soakTest() {
		mapSoakTest(new PersistentHashMap<String, String>());
	}
	
	@Pure
	@ShouldBePure
	@Test(expected=RuntimeException.class)
	public void exTest() {
		new PersistentHashMap<String, String>().assocEx("a", "b").assocEx("a", "c");
		
	}
	
	
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
		PersistentHashMap<String, String> pm2 = new PersistentHashMap<String, String>(pm.seq());
		assertEquals(pm2, pm);
//		
//		// map-based
		PersistentHashMap<String, String> pm3 = new PersistentHashMap<String, String>(pm);
		assertEquals(pm3, pm);
//		 
//		// no-args
		PersistentHashMap<String, String> pm4 = new PersistentHashMap<String, String>();
		for (Entry<String, String> entry : pm) {
			pm4 = pm4.assoc(entry.getKey(), entry.getValue());
		}
		Assert.assertEquals(pm, pm4);

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
	
	
	@Pure
	@ShouldBePure
	@Test
	public void collisionTest() {
		PersistentHashMap<EasyCollider, String> items = new PersistentHashMap<EasyCollider, String>();
		
		for (int i = 0; i < 200; i++) {
			items = items.assoc(new EasyCollider("item"+i), "value"+i);
		}
		
		assertEquals(200, items.size());
		
		for (int i = 0; i < 200; i = i+2) {
			items = items.without(new EasyCollider("item"+i));
		}
		
		assertEquals(100, items.size());
		int count = 0;
		for (Entry<EasyCollider, String> entry : items.entrySeq()) {
			if (entry != null) 
				count ++;
		}
		assertEquals(100, count);
		
		
		
		PersistentHashMap<EasyCollider, String> copy = new PersistentHashMap<EasyCollider, String>(items);
		assertEquals(100, copy.size());
		

		for (int i = 1; i < 200; i = i+2) {
			items = items.without(new EasyCollider("item"+i));
		}
		
		assertEquals(0, items.size());
		
	}
	
	@Pure
	@ShouldBePure
	@Test
	public void constructionCollisionTest() {
		PersistentHashMap<EasyCollider, String> items = new PersistentHashMap<EasyCollider, String>();
		
		for (int i = 0; i < 200; i++) {
			items = items.assoc(new EasyCollider("item"+(i % 10)), "value"+i);
		}
		
		assertEquals(10, items.size());
		
		ISeq<Entry<EasyCollider, String>> seq = items.seq();
		for (Entry<EasyCollider, String> entry : items.seq()) {
			seq = seq.cons(entry);		// doubles the whole seq
		}
		
		items = new PersistentHashMap<EasyCollider, String>(seq);
		assertEquals(10, items.size());
	}

}
