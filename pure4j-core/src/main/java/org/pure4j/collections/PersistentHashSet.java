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

import java.util.List;

public class PersistentHashSet<K> extends APersistentSet<K> {

	static public final PersistentHashSet<Object> EMPTY = new PersistentHashSet<Object>(
			PersistentHashMap.EMPTY);

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> emptySet() {
		return (PersistentHashSet<K>) EMPTY;
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> create(K... init) {
		ITransientSet<K> ret = (ITransientSet<K>) EMPTY.asTransient();
		for (int i = 0; i < init.length; i++) {
			ret = (ITransientSet<K>) ret.conj(init[i]);
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> create(List<K> init) {
		ITransientSet<K> ret = (ITransientSet<K>) EMPTY.asTransient();
		for (K key : init) {
			ret = (ITransientSet<K>) ret.conj(key);
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	static public <K> PersistentHashSet<K> create(ISeq<K> items) {
		ITransientSet<K> ret = (ITransientSet<K>) EMPTY.asTransient();
		for (; items != null; items = items.next()) {
			ret = (ITransientSet<K>) ret.conj(items.first());
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> createWithCheck(K... init) {
		ITransientSet<K> ret = (ITransientSet<K>) EMPTY.asTransient();
		for (int i = 0; i < init.length; i++) {
			ret = (ITransientSet<K>) ret.conj(init[i]);
			if (ret.count() != i + 1)
				throw new IllegalArgumentException("Duplicate key: " + init[i]);
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> createWithCheck(List<K> init) {
		ITransientSet<K> ret = (ITransientSet<K>) EMPTY.asTransient();
		int i = 0;
		for (K key : init) {
			ret = (ITransientSet<K>) ret.conj(key);
			if (ret.count() != i + 1)
				throw new IllegalArgumentException("Duplicate key: " + key);
			++i;
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	@SuppressWarnings("unchecked")
	static public <K> PersistentHashSet<K> createWithCheck(ISeq<K> items) {
		ITransientSet<K> ret = (ITransientSet<K>) EMPTY.asTransient();
		for (int i = 0; items != null; items = items.next(), ++i) {
			ret = (ITransientSet<K>) ret.conj(items.first());
			if (ret.count() != i + 1)
				throw new IllegalArgumentException("Duplicate key: "
						+ items.first());
		}
		return (PersistentHashSet<K>) ret.persistent();
	}

	PersistentHashSet(IPersistentMap<K, K> impl) {
		super(impl);
	}

	public IPersistentSet<K> disjoin(Object key) {
		if (contains(key))
			return new PersistentHashSet<K>(impl.without(key));
		return this;
	}

	public IPersistentSet<K> cons(K o) {
		if (contains(o))
			return this;
		return new PersistentHashSet<K>(impl.assoc(o, o));
	}

	@SuppressWarnings("unchecked")
	public IPersistentCollection<K> empty() {
		return (IPersistentCollection<K>) EMPTY;
	}

	public ITransientCollection<K> asTransient() {
		return new TransientHashSet<K>(((PersistentHashMap<K, K>) impl).asTransient());
	}

	static final class TransientHashSet<K> extends ATransientSet<K> {
		TransientHashSet(ITransientMap<K, K> impl) {
			super(impl);
		}

		public IPersistentCollection<K> persistent() {
			return new PersistentHashSet<K>(impl.persistent());
		}
	}

}
