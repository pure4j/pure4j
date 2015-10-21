package org.pure4j.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class TransientTreeMap<K, V> extends ATransientMap<K, V> {

	public TransientTreeMap() {
		super(new TreeMap<K, V>());
	}
	
	private Comparator<? super K> comp = null;

	public TransientTreeMap(Comparator<? super K> comparator) {
		super(new TreeMap<K, V>(Pure4J.immutable(comparator)));
		this.comp = comparator;
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
	
	public TransientTreeMap(ISeq<Entry<? extends K, ? extends V>> seq) {
		this();
		for (Entry<? extends K, ? extends V> e : seq) {
			this.put(e.getKey(), e.getValue());
		}
	}
	
	
	public TransientTreeMap(Comparator<? super K> comparator, ISeq<Entry<? extends K, ? extends V>> seq) {
		this(Pure4J.immutable(comparator));
		for (Entry<? extends K, ? extends V> e : seq) {
			this.put(e.getKey(), e.getValue());
		}
	}
	
	@Override
	public PersistentTreeMap<K, V> persistent() {
		Comparator<? super K> comparator = PersistentTreeMap.DEFAULT_COMPARATOR;
		if (comp != null) {
			comparator = comp;
		}
		return new PersistentTreeMap<K, V>(comparator, wrapped);
	}


}
