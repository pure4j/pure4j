package org.pure4j.collections;

import java.util.Iterator;

import org.pure4j.annotations.mutable.MutableUnshared;

/**
 * Marker interface for Pure implementations of Iterator.
 * 
 * @author robmoffat
 *
 * @param <E>
 */
@MutableUnshared
public interface IPureIterator<E> extends Iterator<E> {

}
