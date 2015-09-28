/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package org.pure4j.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Pure;

public class PersistentList<K> extends ASeq<K> implements IPersistentList<K> {

	private static final long serialVersionUID = 1L;

	@IgnoreImmutableTypeCheck
	private final K _first;
	private final IPersistentList<K> _rest;
	private final int _count;

	final private static EmptyList<?> EMPTY = new EmptyList<Object>();

	public PersistentList(K first) {
		Pure4J.immutable(first);
		this._first = first;
		this._rest = null;

		this._count = 1;
	}

	protected PersistentList(K _first, IPersistentList<K> _rest, int _count) {
		this._first = _first;
		this._rest = _rest;
		this._count = _count;
	}

	public static <K> IPersistentList<K> create(List<K> init) {
		@SuppressWarnings("unchecked")
		IPersistentList<K> ret = (IPersistentList<K>) EMPTY;
		for (ListIterator<K> i = init.listIterator(init.size()); i.hasPrevious();) {
			ret = (IPersistentList<K>) ret.cons(i.previous());
		}
		return ret;
	}

	public K first() {
		return _first;
	}

	@SuppressWarnings("unchecked")
	public ISeq<K> next() {
		if (_count == 1)
			return null;
		return (ISeq<K>) _rest;
	}

	public K peek() {
		return first();
	}

	public IPersistentList<K> pop() {
		if (_rest == null)
			return empty();
		return _rest;
	}

	public int count() {
		return _count;
	}

	public PersistentList<K> cons(K o) {
		Pure4J.immutable(o);
		return new PersistentList<K>(o, this, _count + 1);
	}

	@SuppressWarnings("unchecked")
	public IPersistentList<K> empty() {
		return (IPersistentList<K>) EMPTY;
	}
	
	@Pure
	@SuppressWarnings("unchecked")
	public static <X> IPersistentList<X> emptyList() {
		return (IPersistentList<X>) EMPTY;
	}
	
	@Pure
	@SuppressWarnings("unchecked")
	public static <X> ISeq<X> emptySeq() {
		return (ISeq<X>) EMPTY;
	}
	
	static class EmptyList<K> implements IPersistentList<K>, ISeq<K> {
		private static final Object[] EMPTY_ARRAY = new Object[] {};
		static final int hasheq = Murmur3.hashOrdered(Collections.EMPTY_LIST);

		public int hashCode() {
			return hasheq;
		}
		
		public boolean equals(Object o) {
			return (o instanceof ISeq || o instanceof List)
					&& PureCollections.seq(o) == null;
		}

		public K first() {
			return null;
		}

		public ISeq<K> next() {
			return null;
		}

//		public ISeq<K> more() {
//			return this;
//		}

		public PersistentList<K> cons(K o) {
			return new PersistentList<K>(o, null, 1);
		}

		public IPersistentCollection<K> empty() {
			return this;
		}

		public K peek() {
			return null;
		}

		public IPersistentList<K> pop() {
			throw new IllegalStateException("Can't pop empty list");
		}

		public int count() {
			return 0;
		}

		public ISeq<K> seq() {
			return this;
		}

		public int size() {
			return 0;
		}

		public boolean isEmpty() {
			return true;
		}

		public boolean contains(Object o) {
			return false;
		}

		public Iterator<K> iterator() {
			return new Iterator<K>() {

				public boolean hasNext() {
					return false;
				}

				public K next() {
					throw new NoSuchElementException();
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		public Object[] toArray() {
			return EMPTY_ARRAY;
		}

		public boolean add(Object o) {
			throw new UnsupportedOperationException();
		}

		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(Collection<? extends K> collection) {
			throw new UnsupportedOperationException();
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection<?> collection) {
			throw new UnsupportedOperationException();
		}

		public boolean removeAll(Collection<?> collection) {
			throw new UnsupportedOperationException();
		}

		public boolean containsAll(Collection<?> collection) {
			return collection.isEmpty();
		}

		public <T> T[] toArray(T[] objects) {
			if (objects.length > 0)
				objects[0] = null;
			return objects;
		}

		// ////////// List stuff /////////////////
		private List<K> reify() {
			return Collections.unmodifiableList(new ArrayList<K>(this));
		}

		public List<K> subList(int fromIndex, int toIndex) {
			return reify().subList(fromIndex, toIndex);
		}

		public K set(int index, Object element) {
			throw new UnsupportedOperationException();
		}

		public K remove(int index) {
			throw new UnsupportedOperationException();
		}

		public int indexOf(Object o) {
			ISeq<K> s = seq();
			for (int i = 0; s != null; s = s.next(), i++) {
				if (Util.equals(s.first(), o))
					return i;
			}
			return -1;
		}

		public int lastIndexOf(Object o) {
			return reify().lastIndexOf(o);
		}

		public ListIterator<K> listIterator() {
			return reify().listIterator();
		}

		public ListIterator<K> listIterator(int index) {
			return reify().listIterator(index);
		}

		@SuppressWarnings("unchecked")
		public K get(int index) {
			return (K) PureCollections.nth(this, index);
		}

		public void add(int index, Object element) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(int index, Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ISeq<K> more() {
			return null;
		}

		@Override
		public ITransientCollection<K> asTransient() {
			return new TransientList<K>();
		}

	}

	@Override
	public ITransientCollection<K> asTransient() {
		return new TransientList<K>(this);
	}

}
