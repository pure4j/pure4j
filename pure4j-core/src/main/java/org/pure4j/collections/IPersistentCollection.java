package org.pure4j.collections;

import java.util.Collection;

import org.pure4j.annotations.immutable.ImmutableValue;

/**
 * Each IPersistentCollection should have:
 * <ul>
 * <li>A no-args constructor for the empty collection</li>
 * <li>A constructor taking an ISeq.</li>
 * <li>A static create method taking a varargs array (impure)</li>
 * <li>An impure method taking another Collection (for copy)</li>
 * </ul>
 * 
 * @author robmoffat
 *
 * @param <K> Base-type for collection elements
 */
@ImmutableValue
public interface IPersistentCollection<K> extends Seqable<K>, Collection<K>, Counted {

	IPersistentCollection<K> cons(K o);

	IPersistentCollection<K> empty();
	
	ITransientCollection<K> asTransient();
	
	//IPersistentCollection<K> addAll(ISeq<? extends K> items);

}
