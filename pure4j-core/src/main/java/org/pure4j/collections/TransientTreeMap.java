package org.pure4j.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class TransientTreeMap<K, V> extends TreeMap<K, V> implements ITransientMap<K, V> {

	public TransientTreeMap() {
		super();
	}

	

	public TransientTreeMap(Comparator<? super K> comparator) {
		super(Pure4J.immutable(comparator));
	}

	public TransientTreeMap(IPersistentMap<? extends K, ? extends V> pt) {
		this(PersistentTreeMap.DEFAULT_COMPARATOR);
		for (Map.Entry<? extends K, ? extends V> entry : pt.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	@Pure(Enforcement.FORCE)
	public TransientTreeMap(Map<? extends K, ? extends V> pt) {
		this(PersistentTreeMap.DEFAULT_COMPARATOR);
		for (Map.Entry<? extends K, ? extends V> entry : pt.entrySet()) {
			Pure4J.immutable(entry.getKey(), entry.getValue());
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	public TransientTreeMap(Comparator<? super K> comp, IPersistentMap<? extends K, ? extends V> pt) {
		this(Pure4J.immutable(comp));
		for (Map.Entry<? extends K, ? extends V> entry : pt.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	@Pure(Enforcement.FORCE)
	public TransientTreeMap(Comparator<? super K> comp, Map<? extends K, ? extends V> pt) {
		this(comp);
		for (Map.Entry<? extends K, ? extends V> entry : pt.entrySet()) {
			Pure4J.immutable(entry.getKey(), entry.getValue());
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	public TransientTreeMap(Seqable<Entry<? extends K, ? extends V>> seq) {
		for (Entry<? extends K, ? extends V> e : seq) {
			this.put(e.getKey(), e.getValue());
		}
	}
	
	
	public TransientTreeMap(Comparator<? super K> comparator, Seqable<Entry<? extends K, ? extends V>> seq) {
		this(comparator);
		for (Entry<? extends K, ? extends V> e : seq) {
			this.put(e.getKey(), e.getValue());
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
