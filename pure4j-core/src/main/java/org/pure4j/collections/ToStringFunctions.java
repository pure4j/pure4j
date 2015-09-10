package org.pure4j.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.pure4j.annotations.pure.Pure;

@Pure
public class ToStringFunctions {

	   /**
     * Returns a string representation of this collection.  The string
     * representation consists of a list of the collection's elements in the
     * order they are returned by its iterator, enclosed in square brackets
     * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
     * <tt>", "</tt> (comma and space).  Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
     *
     * @return a string representation of this collection
     */
    public static <E> String toString(Collection<E> c) {
        Iterator<E> it = c.iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == c ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }
    
	 /**
     * Returns a string representation of this map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this map
     */
    public static <K, V> String toString(Map<K, V> m) {
        Iterator<Entry<K,V>> i = m.entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == m ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == m ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }
}
