package org.pure4j.collections;

import java.util.HashMap;
import java.util.Map;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class TransientHashMap<K, V> extends ATransientMap<K, V> {
	
	public TransientHashMap() {
		super(new HashMap<K, V>());
	}

	public TransientHashMap(int initialCapacity, float loadFactor) {
		super(new HashMap<K, V>(initialCapacity, loadFactor));
	}

	public TransientHashMap(int initialCapacity) {
		super(new HashMap<K, V>(initialCapacity));
	}

	@Pure(Enforcement.FORCE)
	public TransientHashMap(Map<? extends K, ? extends V> m) {
		super(new HashMap<K, V>(m));
	}
	
	public TransientHashMap(IPersistentMap<? extends K, ? extends V> m) {
		super(new HashMap<K, V>(m));
	}
	
	public TransientHashMap(ISeq<Entry<? extends K, ? extends V>> entries) {
		super(new HashMap<K, V>());
		for (Entry<? extends K, ? extends V> entry : entries) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public IPersistentMap<K, V> persistent() {
		return new PersistentHashMap<K,V>(this);
	}

}
