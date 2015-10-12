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

import java.util.List;
import java.util.ListIterator;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class PersistentList<K> extends ASeq<K> implements IPersistentList<K> {

	private static final long serialVersionUID = 1L;

	@IgnoreImmutableTypeCheck
	private final K _first;
	private final IPersistentList<K> _rest;
	private final int _count;

	final private static PersistentList<?> EMPTY = new PersistentList<Object>(null, null, 0);
	
	public PersistentList() {
		this(null, null, 0);
	}

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

	/**
	 * Each element in the list must be immutable.
	 * @param init
	 */
	@Pure(Enforcement.FORCE)	
	public PersistentList(List<K> init) {
		PersistentList<K> ret = emptyList();
		for (ListIterator<K> i = init.listIterator(init.size()); i.hasPrevious();) {
			Pure4J.immutable(i.previous());
			ret = (PersistentList<K>) ret.cons(i.previous());
		}
		this._count = ret.count();
		this._first = ret._first;
		this._rest = ret._rest;
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
	public PersistentList<K> empty() {
		return (PersistentList<K>) EMPTY;
	}
	
	@Pure
	@SuppressWarnings("unchecked")
	public static <X> PersistentList<X> emptyList() {
		return (PersistentList<X>) EMPTY;
	}
	
	@Pure
	@SuppressWarnings("unchecked")
	public static <X> ISeq<X> emptySeq() {
		return (ISeq<X>) EMPTY;
	}
	
	@Override
	public ITransientCollection<K> asTransient() {
		return new TransientList<K>(this);
	}

}
