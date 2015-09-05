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

public abstract class APersistentSet<K> implements IPersistentSet<K>,
		Collection<K>, Set<K>, Serializable {

	int _hasheq = -1;
	final IPersistentMap<K, K> impl;

	protected APersistentSet(IPersistentMap<K, K> impl) {
		this.impl = impl;
	}

	public String toString() {
		return RT.printString(this);
	}

	public boolean contains(Object key) {
		return impl.containsKey(key);
	}

	public K get(Object key) {
		return impl.valAt(key);
	}

	public int count() {
		return impl.count();
	}

	@SuppressWarnings("unchecked")
	public ISeq<K> seq() {
		return (ISeq<K>) RT.keys(impl);
	}

	public Object invoke(Object arg1) {
		return get(arg1);
	}

	public boolean equals(Object obj) {
		return setEquals(this, obj);
	}

	static public boolean setEquals(IPersistentSet<?> s1, Object obj) {
		if (s1 == obj)
			return true;
		if (!(obj instanceof Set))
			return false;
		Set<?> m = (Set<?>) obj;

		if (m.size() != s1.count())
			return false;

		for (Object aM : m) {
			if (!s1.contains(aM))
				return false;
		}

		return true;
	}

	public int hashCode() {
		if (_hasheq == -1) {
			_hasheq = Murmur3.hashUnordered(this);
		}
		return _hasheq;
	}

	public Object[] toArray() {
		return RT.seqToArray(seq());
	}

	public boolean add(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends K> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		return (T[]) RT.seqToPassedArray(seq(), a);
	}

	public int size() {
		return count();
	}

	public boolean isEmpty() {
		return count() == 0;
	}

	public Iterator<K> iterator() {
		if (impl instanceof IMapIterable)
			return ((IMapIterable<K, ?>) impl).keyIterator();
		else
			return new Iterator<K>() {
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

}
