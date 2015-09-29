package org.pure4j.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TransientTreeMap<K, V> extends TreeMap<K, V> implements ITransientMap<K, V> {

	public TransientTreeMap() {
		super();
	}


	public TransientTreeMap(Comparator<? super K> comparator) {
		super(comparator);
	}

	public TransientTreeMap(PersistentTreeMap<K, V> pt) {
		this(pt.comparator());
		for (Map.Entry<K, V> entry : pt.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public int count() {
		return size();
	}

	
	@Override
	public IPersistentMap<K, V> persistent() {
		return PersistentTreeMap.create(comparator(), this);
	}


}
