package org.pure4j.collections;

import java.util.HashMap;
import java.util.Map;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class TransientHashMap<K, V> extends HashMap<K, V> implements ITransientMap<K, V> {
	
	public TransientHashMap() {
		super();
	}

	public TransientHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public TransientHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	@Pure(Enforcement.FORCE)
	public TransientHashMap(Map<? extends K, ? extends V> m) {
		super(m);
	}
	
	public TransientHashMap(IPersistentMap<? extends K, ? extends V> m) {
		super(m);
	}
	
	public TransientHashMap(Seqable<Entry<? extends K, ? extends V>> entries) {
		super();
		for (Entry<? extends K, ? extends V> entry : entries) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public IPersistentMap<K, V> persistent() {
		return new PersistentHashMap<K,V>(this);
	}

}
