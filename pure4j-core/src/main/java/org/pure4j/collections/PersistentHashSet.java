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

import java.util.Collection;
import java.util.List;

import org.pure4j.Pure4J;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

public class PersistentHashSet<K> extends APersistentSet<K> {

	static private final PersistentHashSet<Object> EMPTY = new PersistentHashSet<Object>(
			PersistentHashMap.emptyMap());

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> emptySet() {
		return (PersistentHashSet<K>) EMPTY;
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> create(K... init) {
		ITransientSet<K> ret = (ITransientSet<K>) new TransientHashSet<>();
		for (int i = 0; i < init.length; i++) {
			ret.add(init[i]);
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> create(Collection<K> init) {
		ITransientSet<K> ret = (ITransientSet<K>)  new TransientHashSet<>();
		for (K key : init) {
			ret.add(key);
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	static public <K> PersistentHashSet<K> create(ISeq<K> items) {
		ITransientSet<K> ret = (ITransientSet<K>)  new TransientHashSet<>();
		for (; items != null; items = items.next()) {
			ret.add(items.first());
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> createWithCheck(K... init) {
		ITransientSet<K> ret = (ITransientSet<K>)  new TransientHashSet<>();
		for (int i = 0; i < init.length; i++) {
			ret.add(init[i]);
			if (ret.size() != i + 1)
				throw new IllegalArgumentException("Duplicate key: " + init[i]);
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> createWithCheck(List<K> init) {
		ITransientSet<K> ret = (ITransientSet<K>)  new TransientHashSet<>();
		int i = 0;
		for (K key : init) {
			ret.add(key);
			if (ret.size() != i + 1)
				throw new IllegalArgumentException("Duplicate key: " + key);
			++i;
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	static public <K> PersistentHashSet<K> createWithCheck(ISeq<K> items) {
		ITransientSet<K> ret = (ITransientSet<K>)  new TransientHashSet<>();
		for (int i = 0; items != null; items = items.next(), ++i) {
			ret.add(items.first());
			if (ret.size() != i + 1)
				throw new IllegalArgumentException("Duplicate key: "
						+ items.first());
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	PersistentHashSet(IPersistentMap<K, K> impl) {
		super(impl);
	}

	public IPersistentSet<K> disjoin(Object key) {
		Pure4J.immutable(key);
		if (contains(key))
			return new PersistentHashSet<K>(impl.without(key));
		return this;
	}

	public IPersistentSet<K> cons(K o) {
		Pure4J.immutable(o);
		if (contains(o))
			return this;
		return new PersistentHashSet<K>(impl.assoc(o, o));
	}

	@SuppressWarnings("unchecked")
	public PersistentHashSet<K> empty() {
		return (PersistentHashSet<K>) EMPTY;
	}

	@Pure(Enforcement.NOT_PURE)
	public ITransientCollection<K> asTransient() {
		return new TransientHashSet<>(this);
	}

}
