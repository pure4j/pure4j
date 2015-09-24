package org.pure4j.collections;

/**
 * Marker interface to show that a persistent collection can become transient.
 * 
 * @author robmoffat
 *
 * @param <K>
 */
public interface Transientable<K> extends IPersistentCollection<K> {

	public ITransientCollection<K> asTransient();
}
