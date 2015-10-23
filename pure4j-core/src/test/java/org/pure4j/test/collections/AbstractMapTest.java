package org.pure4j.test.collections;

import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.IterableSeq;
import org.pure4j.collections.MapEntry;
import org.pure4j.collections.PersistentHashSet;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class AbstractMapTest extends AbstractTest {


	@Pure
	@ShouldBePure
	public static void mapSoakTest(IPersistentMap<String, String> someMap) {
		someMap = someMap.empty();
		
		assertEquals(null, someMap.seq());
		assertEquals(new PersistentHashSet<String>(), someMap.keySet());
		assertEquals(0, someMap
			.assoc(null,"verg")
			.without(null)
			.assoc("a", "b")
			.assoc("a", "c")
			.without("a").size());
		
		int length = 12000;
		for (int i = 0; i < length; i++) {
			someMap = someMap.assoc("key"+i, "value_old"+i);
		}

		assertEquals(length, someMap.size());
		
		// replace some stuff
		for (int i = 0; i < length ; i++) {
			someMap = someMap.assoc("key"+i, "value"+i);
		}
		
		assertEquals(length, someMap.size());
		
		assertEquals("value3", someMap.get("key3"));
		assertEquals(new MapEntry<String, String>("key3", "value3"), someMap.entryAt("key3"));

		// iterate over things
		PersistentHashSet<String> keys = new PersistentHashSet<String>(createArray("key", length));
		PersistentHashSet<String> values = new PersistentHashSet<String>(createArray("value", length));
		
		assertEquals(keys, new PersistentHashSet<String>(someMap.keySeq()));
		assertEquals(values,  new PersistentHashSet<String>(someMap.valueSeq()));
		
		assertEquals(keys, new PersistentHashSet<String>(new IterableSeq<String>(someMap.keyIterator())));
		assertEquals(values, new PersistentHashSet<String>(new IterableSeq<String>(someMap.valIterator())));
		
		// remove some stuff
		for (int i = 0; i < length / 2; i++) {
			someMap = someMap.without("key"+i);
		}
		
		assertEquals(length/2, someMap.size());
		
		someMap = someMap.assoc(null, "SomeNullThing");
		assertEquals(length/2+1, someMap.seq().size());
		assertEquals("SomeNullThing", someMap.get(null));
		assertEquals(new MapEntry<String, String>(null, "SomeNullThing"), someMap.entryAt(null));
		
		
		
	}
		

	@Pure
	@ShouldBePure
	private static String[] createArray(String prefix, int len) {
		String[] out = new String[len];
		for (int i = 0; i < out.length; i++) {
			out[i] = prefix+i;
		}
		return out;
	}
}
