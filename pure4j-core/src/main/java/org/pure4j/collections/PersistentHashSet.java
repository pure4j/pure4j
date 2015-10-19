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

import java.util.Arrays;
import java.util.Collection;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureParameters;

public class PersistentHashSet<K> extends APersistentSet<K> {

	static private final PersistentHashSet<Object> EMPTY = new PersistentHashSet<Object>(
			PersistentHashMap.emptyMap());

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> emptySet() {
		return (PersistentHashSet<K>) EMPTY;
	}

	@Pure(Enforcement.FORCE)
	@SuppressWarnings("unchecked")
	public PersistentHashSet(K... init) {
		this(createTemporaryMap(Arrays.asList(init)));
	}

	@Pure(Enforcement.FORCE)
	public PersistentHashSet(Collection<K> init) {
		this(createTemporaryMap(init));
	}
	
	public PersistentHashSet(IPersistentSet<K> init) {
		this(createTemporaryMap(init));
	}

	public PersistentHashSet(ISeq<K> items) {
		this(createTemporaryMap(items));
	}

	@Pure
	@PureParameters(Enforcement.NOT_PURE)
	private static <K> IPersistentMap<K, K> createTemporaryMap(Iterable<K> items) {
		PersistentHashMap<K, K> phm = new PersistentHashMap<K, K>();
		for (K k : items) {
			Pure4J.immutable(k);
			phm = phm.assoc(k, k);
		}
		
		return phm;
	}

	private PersistentHashSet(IPersistentMap<K, K> impl) {
		super(impl);
	}

	public IPersistentSet<K> disjoin(Object key) {
		Pure4J.immutable(key);
		if (contains(key))
			return new PersistentHashSet<K>(impl.without(key));
		return this;
	}

	public PersistentHashSet<K> cons(K o) {
		Pure4J.immutable(o);
		if (contains(o))
			return this;
		return new PersistentHashSet<K>(impl.assoc(o, o));
	}

	@SuppressWarnings("unchecked")
	public PersistentHashSet<K> empty() {
		return (PersistentHashSet<K>) EMPTY;
	}

	public ITransientSet<K> asTransient() {
		return new TransientHashSet<K>(this);
	}

	@Override
	public PersistentHashSet<K> addAll(ISeq<? extends K> items) {
		IPersistentSet<K> out = this;
		for (K k : items) {
			out = out.cons(k);
		}
		return (PersistentHashSet<K>) out;
	}

}
