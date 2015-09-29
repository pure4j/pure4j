package org.pure4j.collections;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.pure4j.Pure4J;
import org.pure4j.collections.PersistentHashMap.BitmapIndexedNode;

public final class TransientHashMap<K, V> extends ATransientMap<K, V> {
	private final AtomicReference<Thread> edit;
	private volatile PersistentHashMap.INode root;
	private volatile int count;
	private volatile boolean hasNull;
	private volatile V nullValue;
	private final Box leafFlag = new Box(null);

	TransientHashMap(PersistentHashMap<K, V> m) {
		this(new AtomicReference<Thread>(Thread.currentThread()), m.root,
				m.count, m.hasNull, m.nullValue);
	}

	TransientHashMap(AtomicReference<Thread> edit, PersistentHashMap.INode root, int count,
			boolean hasNull, V nullValue) {
		this.edit = edit;
		this.root = root;
		this.count = count;
		this.hasNull = hasNull;
		this.nullValue = nullValue;
	}

	TransientHashMap<K, V> doAssoc(K key, V val) {
		if (key == null) {
			if (this.nullValue != val)
				this.nullValue = val;
			if (!hasNull) {
				this.count++;
				this.hasNull = true;
			}
			return this;
		}
		// Box leafFlag = new Box(null);
		leafFlag.val = null;
		PersistentHashMap.INode n = (root == null ? BitmapIndexedNode.EMPTY : root).assoc(
				edit, 0, PersistentHashMap.hash(key), key, val, leafFlag);
		if (n != this.root)
			this.root = n;
		if (leafFlag.val != null)
			this.count++;
		return this;
	}

	TransientHashMap<K, V> doWithout(Object key) {
		if (key == null) {
			if (!hasNull)
				return this;
			hasNull = false;
			nullValue = null;
			this.count--;
			return this;
		}
		if (root == null)
			return this;
		// Box leafFlag = new Box(null);
		leafFlag.val = null;
		PersistentHashMap.INode n = root.without(edit, 0, PersistentHashMap.hash(key), key, leafFlag);
		if (n != root)
			this.root = n;
		if (leafFlag.val != null)
			this.count--;
		return this;
	}

	PersistentHashMap<K, V> doPersistent() {
		edit.set(null);
		return new PersistentHashMap<K, V>(count, root, hasNull, nullValue);
	}

	@SuppressWarnings("unchecked")
	V doValAt(Object key, V notFound) {
		if (key == null)
			if (hasNull)
				return nullValue;
			else
				return notFound;
		if (root == null)
			return notFound;
		return (V) root.find(0, PersistentHashMap.hash(key), key, notFound);
	}

	int doCount() {
		return count;
	}

	void ensureEditable() {
		if (edit.get() == null)
			throw new IllegalAccessError(
					"Transient used after persistent! call");
	}

	@Override
	public boolean containsKey(Object key) {
		Pure4J.immutable(key);
		if (key == null)
			return hasNull;
		return (root != null) ? root.find(0, PersistentHashMap.hash(key), key, PersistentHashMap.NOT_FOUND) != PersistentHashMap.NOT_FOUND
				: false;
	}

	/**
	 * Currently very expensive
	 */
	@Override
	public boolean containsValue(Object value) {
		return values().contains(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		this.root = PersistentHashMap.EMPTY.root;
		this.count = 0;
		this.hasNull = PersistentHashMap.EMPTY.hasNull;
		this.nullValue = (V) PersistentHashMap.EMPTY.nullValue;
	}

	@Override
	public Set<K> keySet() {
		return persistent().keySet();
	}

	@Override
	public Collection<V> values() {
		return persistent().values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return persistent().entrySet();
	}
}