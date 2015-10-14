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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public abstract class ASeq<K> implements ISeq<K>, Serializable {

	private static final long serialVersionUID = 220865945544862915L;
	
	@IgnoreImmutableTypeCheck
	protected transient int _hasheq = -1;

	public String toString() {
		return ToStringFunctions.toString(this);
	}

	@SuppressWarnings("unchecked")
	public IPersistentCollection<K> empty() {
		return (IPersistentCollection<K>) PersistentList.emptyList();
	}

	protected ASeq() {
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Seqable) {
			ISeq<?> ms = ((Seqable<?>)obj).seq();
			for (ISeq<K> s = seq(); s != null; s = s.next(), ms = ms.next()) {
				if (ms == null || !Util.equals(s.first(), ms.first()))
					return false;
			}
			return ms == null;
		} else if (obj instanceof Iterable) {
			Iterator<K> i1 = this.iterator();
			Iterator<?> i2 = ((Iterable<?>) obj).iterator();
			if (i1.hasNext() && (i2.hasNext())) {
				if (!Util.equals(i1.next(), i2.next())) {
					return false;
				}
			} else if (i1.hasNext() != i2.hasNext()) {
				return false;
			}
			
			return true;
		}
		
		return false;
	}

	public int hashCode() {
		if (_hasheq == -1) {
			_hasheq = Hasher.hashOrdered(this);
		}
		return _hasheq;
	}

	public int size() {
		int i = 1;
		for (ISeq<K> s = next(); s != null; s = s.next(), i++)
			if (s instanceof Counted)
				return i + s.size();
		return i;
	}

	final public ISeq<K> seq() {
		return this;
	}

	public ISeq<K> cons(K o) {
		Pure4J.immutable(o);
		return new Cons<K>(o, this);
	}

	public ISeq<K> more() {
		ISeq<K> s = next();
		if (s == null)
			return PersistentList.emptySeq();
		return s;
	}

	public Object[] toArray() {
		return PureCollections.seqToArray(seq());
	}
	
	@SuppressWarnings("unchecked")
	@Pure(Enforcement.FORCE)
	public <T> T[] toArray(T[] a) {
		return (T[]) PureCollections.seqToNewArray(seq(), a);
	}

	public boolean add(K o) {
		Pure4J.unsupported();
		return true;
	}

	public boolean remove(Object o) {
		Pure4J.unsupported();
		return true;
	}

	public boolean addAll(Collection<? extends K> c) {
		Pure4J.unsupported();
		return true;
	}

	public void clear() {
		Pure4J.unsupported();
	}

	public boolean retainAll(Collection<?> c) {
		Pure4J.unsupported();
		return true;
	}

	public boolean removeAll(Collection<?> c) {
		Pure4J.unsupported();
		return true;
	}

	public boolean containsAll(Collection<?> c) {
		Pure4J.immutable(c);
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return seq() == null;
	}

	public boolean contains(Object o) {
		Pure4J.immutable(o);
		for (ISeq<K> s = seq(); s != null; s = s.next()) {
			if (Util.equals(s.first(), o))
				return true;
		}
		return false;
	}

	public IPureIterator<K> iterator() {
		return new SeqIterator<K>(this);
	}

	// ////////// List stuff /////////////////
	private List<K> reify() {
		return new TransientList<K>(this);
	}

	public List<K> subList(int fromIndex, int toIndex) {
		return reify().subList(fromIndex, toIndex);
	}

	public K set(int index, Object element) {
		Pure4J.unsupported();
		return null;
	}

	public K remove(int index) {
		Pure4J.unsupported();
		return null;
	}

	public int indexOf(Object o) {
		Pure4J.immutable(o);
		ISeq<K> s = seq();
		for (int i = 0; s != null; s = s.next(), i++) {
			if (Util.equals(s.first(), o))
				return i;
		}
		return -1;
	}

	public int lastIndexOf(Object o) {
		Pure4J.immutable(o);
		return reify().lastIndexOf(o);
	}

	
	public IPureListIterator<K> listIterator() {
		return new PureListIterator<K>(reify().listIterator());
	}

	public IPureListIterator<K> listIterator(int index) {
		return new PureListIterator<K>(reify().listIterator(index));
	}

	@SuppressWarnings("unchecked")
	public K get(int index) {
		return (K) PureCollections.nth(this, index);
	}

	public void add(int index, Object element) {
		Pure4J.unsupported();
	}

	public boolean addAll(int index, Collection<? extends K> c) {
		Pure4J.unsupported();
		return true;
	}

}
