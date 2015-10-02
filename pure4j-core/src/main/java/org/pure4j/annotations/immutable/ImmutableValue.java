package org.pure4j.annotations.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pure4j.annotations.pure.Pure;

/**
 * See: <a href="http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/ImmutableValue.html">Online Spec</a>
 *
 * @see org.pure4j.annotations.immutable.ImmutableValue
 * @see org.pure4j.annotations.pure.Pure
 * @see org.pure4j.annotations.mutable.MutableUnshared

 * 
 * @author robmoffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ImmutableValue {

}
