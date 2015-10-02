package org.pure4j.annotations.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to declare the immutability of a class which is not within your codebase.
 * 
 * @see org.pure4j.annotations.immutable.ImmutableValue
 * @see org.pure4j.annotations.pure.Pure
 * @see org.pure4j.annotations.mutable.MutableUnshared
 * 
 * @author robmoffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeclareImmutable {

	Class<?> value();
}
