package org.pure4j.examples.tutorial_mutable_unshared;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.collections.IPersistentMap;
import org.pure4j.collections.PersistentHashMap;
import org.pure4j.collections.TransientHashMap;

@MutableUnshared
public class CharacterHistogram {

	private TransientHashMap<Character, Integer> counts = new TransientHashMap<Character, Integer>();
	
	public void countCharacters(String in) {
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			Integer count = counts.get(c);
			if (count == null) {
				counts.put(c, 1);
			} else {
				counts.put(c,1 + count);
			}
			
		}
	}
	
	public IPersistentMap<Character, Integer> getCounts() {
		return new PersistentHashMap<Character, Integer>(counts);
	}
}
