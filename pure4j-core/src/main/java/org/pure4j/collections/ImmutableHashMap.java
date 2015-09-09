package org.pure4j.collections;

public class ImmutableHashMap<K, V> extends PersistentHashMap<K, V>{

	ImmutableHashMap(int count, org.pure4j.collections.PersistentHashMap.INode root,
			boolean hasNull, V nullValue) {
		super(count, root, hasNull, nullValue);
		// TODO Auto-generated constructor stub
	}

}
