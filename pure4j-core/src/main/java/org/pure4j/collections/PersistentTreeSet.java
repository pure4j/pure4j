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

import java.util.Comparator;
import java.util.Set;

import org.pure4j.Pure4J;

public class PersistentTreeSet<K> extends APersistentSet<K> implements Reversible<K>, Sorted<K, K> {

	static private final PersistentTreeSet<Object> EMPTY = new PersistentTreeSet<Object>(PersistentTreeMap.emptyMap());

	public PersistentTreeSet(Seqable<K> items) {
		this(PersistentTreeMap.DEFAULT_COMPARATOR, items);
	}
	
	@SafeVarargs
	public PersistentTreeSet(K... items) {
		this(PersistentTreeMap.DEFAULT_COMPARATOR, items);
	}
	
	@SafeVarargs
	public PersistentTreeSet(Comparator<? super K> comp, K... items) {
		this(PersistentTreeMap.createTemporary(comp, items, true));
	}
	
	public PersistentTreeSet() {
		this(PersistentTreeMap.DEFAULT_COMPARATOR);
	}
	
	public PersistentTreeSet(Comparator<? super K> comp) {
		this(new PersistentTreeMap<K, K>(comp));
	}
	
	public PersistentTreeSet(Comparator<? super K> comp, ISeq<K> items) {
		super(APersistentSet.createMap(items, new TransientTreeMap<K, K>(comp)));
	}

	private PersistentTreeSet(IPersistentMap<K, K> impl) {
		super(impl);
	}
	

	public PersistentTreeSet(IPersistentSet<K> in) {
		this(PersistentTreeMap.DEFAULT_COMPARATOR, in);
	}
	
	public PersistentTreeSet(Comparator<? super K> comp, IPersistentSet<K> in) {
		this(createTemporaryMap(comp, in));
	}

	
	public PersistentTreeSet(Set<K> in) {
		this(PersistentTreeMap.DEFAULT_COMPARATOR, in);
	}
	
	public PersistentTreeSet(Comparator<? super K> comp, Set<K> in) {
		this(createTemporaryMap(comp, in));
	}

	public PersistentTreeSet(Comparator<? super K> comp, Seqable<K> items) {
		this(createTemporaryMap(comp, items));
	}
	
	protected static <K> PersistentTreeMap<K, K> createTemporaryMap(Comparator<? super K> comp, Iterable<K> in) {
		PersistentTreeMap<K,K> ret = new PersistentTreeMap<K, K>(comp);
		for (K k : in) {
			Pure4J.immutable(k);
			ret = ret.assoc(k, k);
		}
		return ret;
	}

	public PersistentTreeSet<K> disjoin(Object key) {
		Pure4J.immutable(key);
		if (contains(key))
			return new PersistentTreeSet<K>(impl.without(key));
		return this;
	}

	public PersistentTreeSet<K> cons(K o) {
		Pure4J.immutable(o);
		if (contains(o))
			return this;
		return new PersistentTreeSet<K>(impl.assoc(o, o));
	}

	@SuppressWarnings("unchecked")
	public PersistentTreeSet<K> empty() {
		return (PersistentTreeSet<K>) EMPTY;
	}
	
	@SuppressWarnings("unchecked")
	public static <K> PersistentTreeSet<K> emptySet() {
		return (PersistentTreeSet<K>) EMPTY;
	}

	public ISeq<K> rseq() {
		return seq(false);
	}


	public Comparator<? super K> comparator() {
		return ((PersistentTreeMap<K, K>) impl).comparator();
	}

	@SuppressWarnings("unchecked")
	public K entryKey(Object entry) {
		Pure4J.immutable(entry);
		return (K) entry;
	}

	public ISeq<K> seq(boolean ascending) {
		PersistentTreeMap<K, K> m = (PersistentTreeMap<K, K>) impl;
		return APersistentMap.KeySeq.createFromMap(m);
	}

	public ISeq<K> seqFrom(K key, boolean ascending) {
		Pure4J.immutable(key);
		PersistentTreeMap<K, K> m = (PersistentTreeMap<K, K>) impl;
		return APersistentMap.KeySeq.create(m.seq());
	}

	@Override
	public ITransientSet<K> asTransient() {
		return new TransientTreeSet<K>(comparator(), this.seq());
	}
	
	@Override
	public PersistentTreeSet<K> addAll(ISeq<? extends K> items) {
		IPersistentSet<K> out = this;
		for (K k : items) {
			out = out.cons(k);
		}
		return (PersistentTreeSet<K>) out;
	}
}
