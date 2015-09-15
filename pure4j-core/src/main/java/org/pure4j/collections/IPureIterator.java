package org.pure4j.collections;

import java.util.Iterator;

import org.pure4j.annotations.pure.Pure;

/**
 * Marker interface for Pure implementations of Iterator.
 * 
 * @author robmoffat
 *
 * @param <E>
 */
@Pure
public interface IPureIterator<E> extends Iterator<E> {

}
