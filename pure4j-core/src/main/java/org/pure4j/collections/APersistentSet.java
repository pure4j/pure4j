/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Mar 3, 2008 */

package org.pure4j.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureInterface;

public abstract class APersistentSet<K> implements IPersistentSet<K>,
		Collection<K>, Set<K>, Serializable {

	@IgnoreImmutableTypeCheck
	int _hasheq = -1;
	
	final IPersistentMap<K, K> impl;

	protected APersistentSet(IPersistentMap<K, K> impl) {
		this.impl = impl;
	}

	public String toString() {
		return ToStringFunctions.toString(this);
	}

	public boolean contains(Object key) {
		Pure4J.immutable(key);
		return impl.containsKey(key);
	}

	public int size() {
		return impl.size();
	}

	public ISeq<K> seq() {
		return APersistentMap.KeySeq.createFromMap(impl);
	}

	public boolean equals(Object obj) {
		return setEquals(this, obj);
	}

	@Pure
	static public boolean setEquals(IPersistentSet<?> s1, Object obj) {
		Pure4J.immutable(obj);
		if (s1 == obj)
			return true;
		if (!(obj instanceof Set))
			return false;
		Set<?> m = (Set<?>) obj;

		if (m.size() != s1.size())
			return false;

		for (Object aM : m) {
			if (!s1.contains(aM))
				return false;
		}

		return true;
	}

	public int hashCode() {
		if (_hasheq == -1) {
			_hasheq = Hasher.hashUnordered(this);
		}
		return _hasheq;
	}

	public Object[] toArray() {
		return PureCollections.seqToArray(seq());
	}

	public boolean add(Object o) {
		Pure4J.immutable(o);
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		Pure4J.immutable(o);
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends K> c) {
		Pure4J.immutable(c);
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		Pure4J.immutable(c);
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		Pure4J.immutable(c);
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		Pure4J.immutable(c);
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Pure(Enforcement.FORCE)	// due to param
	public <T> T[] toArray(T[] a) {
		return (T[]) PureCollections.seqToNewArray(seq(), a, false);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public Iterator<K> iterator() {
		return new IPureIterator<K>() {
			private final Iterator<Entry<K, K>> iter = impl.iterator();

			public boolean hasNext() {
				return iter.hasNext();
			}

			public K next() {
				return iter.next().getKey();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Pure
	@PureInterface(Enforcement.NOT_PURE)
	protected static <K> IPersistentMap<K, K> createMap(ISeq<K> items, ITransientMap<K, K> map) {
		for (; items != null; items = items.next()) {
			K first = items.first();
			map.put(first, first);
		}
		return map.persistent();
	}
	
	@Pure
	@PureInterface(Enforcement.NOT_PURE)
	private static <K> IPersistentMap<K, K> createMap(Collection<K> init, ITransientMap<K, K> map ) {
		for (K key : init) {
			map.put(key, key);
		}
		return map.persistent();
	}

}
