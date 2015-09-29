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

	@SuppressWarnings("unchecked")
	public static <K> PersistentHashSet<K> create(K... init) {
		ITransientMap<K, K> map = (ITransientMap<K, K>) PersistentHashMap.emptyMap().asTransient();
		for (int i = 0; i < init.length; i++) {
			map.put(init[i], init[i]);
		}
		return new PersistentHashSet<K>(map.persistent());
	}

	public PersistentHashSet(Collection<K> init) {
		this(createMap(init));
	}

	

	public PersistentHashSet(ISeq<K> items) {
		this(createMap(items));
	}

	@Pure
	@PureParameters(Enforcement.NOT_PURE)
	private static <K> IPersistentMap<K, K> createMap(ISeq<K> items) {
		ITransientMap<K, K> map = new TransientHashMap<K, K>();
		for (; items != null; items = items.next()) {
			K first = items.first();
			map.put(first, first);
		}
		return map.persistent();
	}
	
	@Pure
	@PureParameters(Enforcement.NOT_PURE)
	private static <K> IPersistentMap<K, K> createMap(Collection<K> init) {
		ITransientMap<K, K> map = new TransientHashMap<K, K>();
		for (K key : init) {
			map.put(key, key);
		}
		return map.persistent();
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

	public ITransientSet<K> asTransient() {
		return new TransientHashSet<>(this);
	}

}
