package org.pure4j.collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.pure4j.Pure4J;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureInterface;

@MutableUnshared
public abstract class ATransientMap<K, V> implements ITransientMap<K, V> {

	protected Map<K,V> wrapped;

	protected ATransientMap(Map<K, V> wrapped) {
		super();
		this.wrapped = wrapped;
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Pure(Enforcement.FORCE)	// allows a more permissive interface than Pure4J.immutable()
	@Override
	public boolean containsKey(Object key) {
		return wrapped.containsKey(key);
	}

	@Pure(Enforcement.FORCE)	// allows a more permissive interface than Pure4J.immutable()
	@Override
	public boolean containsValue(Object value) {
		return wrapped.containsValue(value);
	}

	@Pure(Enforcement.FORCE)	// allows a more permissive interface than Pure4J.immutable()
	@Override
	public V get(Object key) {
		return wrapped.get(key);
	}

	@Override
	public V put(K key, V value) {
		Pure4J.immutable(key, value);
		return Pure4J.returnImmutable(wrapped.put(key, value));
	}

	@Pure(Enforcement.FORCE)	// allows a more permissive interface than Pure4J.immutable()
	@Override
	public V remove(Object key) {
		return Pure4J.returnImmutable(wrapped.remove(key));
	}

	@PureInterface(Enforcement.NOT_PURE)
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> ent : m.entrySet()) {
			Pure4J.immutable(ent.getKey(), ent.getValue());
			wrapped.put(ent.getKey(), ent.getValue());
		}
	}

	@Override
	public void clear() {
		wrapped.clear();
	}

	@PureInterface(Enforcement.NOT_PURE)	// since this shares state - changes to map affect the returned set.
	@Override
	public Set<K> keySet() {
		return wrapped.keySet();
	}

	@PureInterface(Enforcement.NOT_PURE) // since this shares state - changes to map affect the returned set.
	@Override
	public Collection<V> values() {
		return wrapped.values();
	}

	@PureInterface(Enforcement.NOT_PURE)
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return wrapped.entrySet();
	}
	
	public ISeq<K> keySeq() {
		return new IterableSeq<K>(keySet());
	}
	
	public ISeq<V> valueSeq() {
		return new IterableSeq<V>(values());
	}
	
	public ISeq<Entry<K, V>> entrySeq() {
		return new IterableSeq<Map.Entry<K,V>>(entrySet());
	}
}
