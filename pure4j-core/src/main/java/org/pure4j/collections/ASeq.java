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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public abstract class ASeq<K> implements ISeq<K>, Sequential, List<K>, Serializable {

	private static final long serialVersionUID = 220865945544862915L;
	
	@ImmutableValue(Enforcement.FORCE)
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
		if (!(obj instanceof ISeq))
			return false;
		ISeq<?> ms = (ISeq<?>) obj;
		for (ISeq<K> s = seq(); s != null; s = s.next(), ms = ms.next()) {
			if (ms == null || !Util.equals(s.first(), ms.first()))
				return false;
		}
		return ms == null;
	}

	public int hashCode() {
		if (_hasheq == -1) {
			_hasheq = Murmur3.hashOrdered(this);
		}
		return _hasheq;
	}

	public int count() {
		int i = 1;
		for (ISeq<K> s = next(); s != null; s = s.next(), i++)
			if (s instanceof Counted)
				return i + s.count();
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

	public int size() {
		return count();
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

	public Iterator<K> iterator() {
		return new SeqIterator<K>(this);
	}

	// ////////// List stuff /////////////////
	@Pure(Enforcement.FORCE)  // no side-effects
	private List<K> reify() {
		return Collections.unmodifiableList(new ArrayList<K>(this));
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
		Pure4J.unsupported();
	}

	public boolean addAll(int index, Collection<? extends K> c) {
		Pure4J.unsupported();
		return true;
	}

}
