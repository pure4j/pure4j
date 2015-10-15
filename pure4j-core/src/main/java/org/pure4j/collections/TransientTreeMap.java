package org.pure4j.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.pure4j.Pure4J;

public class TransientTreeMap<K, V> extends TreeMap<K, V> implements ITransientMap<K, V> {

	public TransientTreeMap() {
		super();
	}


	public TransientTreeMap(Comparator<? super K> comparator) {
		super(Pure4J.immutable(comparator));
	}

	public TransientTreeMap(PersistentTreeMap<K, V> pt) {
		this(pt.comparator());
		for (Map.Entry<K, V> entry : pt.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public int size() {
		return size();
	}

	
	@Override
	public PersistentTreeMap<K, V> persistent() {
		return new PersistentTreeMap<K, V>(comparator(), this);
	}


}
