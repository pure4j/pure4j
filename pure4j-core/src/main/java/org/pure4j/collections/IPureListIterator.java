package org.pure4j.collections;

import java.util.ListIterator;

import org.pure4j.annotations.pure.Pure;

/**
 * Marker interface for Pure implementations of Iterator.
 * 
 * @author robmoffat
 *
 * @param <E>
 */
@Pure
public interface IPureListIterator<E> extends ListIterator<E> {

}
