
package org.pure4j.annotations.mutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.ImmutableValue;

/**
 * See: <a href="http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/MutableUnshared.html">Online Spec</a>
 * @author robmoffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MutableUnshared {

}
