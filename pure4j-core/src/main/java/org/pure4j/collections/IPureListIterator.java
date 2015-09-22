package org.pure4j.collections;

import java.util.ListIterator;

import org.pure4j.annotations.mutable.MutableUnshared;

/**
 * Marker interface for Pure implementations of Iterator.
 * 
 * @author robmoffat
 *
 * @param <E>
 */
@MutableUnshared
public interface IPureListIterator<E> extends ListIterator<E> {

}
