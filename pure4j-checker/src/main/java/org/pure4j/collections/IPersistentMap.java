/**
 * Copyright (c) Rich Hickey. All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 */

package org.pure4j.collections;

import java.util.Map;
import java.util.Map.Entry;

import org.pure4j.annotations.immutable.ImmutableValue;

@ImmutableValue
public interface IPersistentMap<K, V> extends Iterable<Map.Entry<K, V>>, Counted, IMapIterable<K, V>, Map<K, V>, Seqable<Entry<K, V>> {

	IPersistentMap<K, V> assoc(K key, V val);

	IPersistentMap<K, V> assocEx(K key, V val);

	IPersistentMap<K, V> without(Object key);

	IMapEntry<K, V> entryAt(Object key);

	V get(Object key, V notFound);
	
	ITransientMap<K, V> asTransient();
	
	ISeq<K> keySeq();
	
	ISeq<V> valueSeq();
	
	IPersistentMap<K, V> empty();

}
