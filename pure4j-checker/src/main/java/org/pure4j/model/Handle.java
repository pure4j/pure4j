package org.pure4j.model;


/**
 * Lightweight interface to some java reflection construct.
 *
 */
public interface Handle<X> {

    /**
     * Returns the reflection-object that this is a handle for
     * @return the object being handled.
     * @param cl a classloader to hydrate with
     */
    public X hydrate(ClassLoader cl);
}
