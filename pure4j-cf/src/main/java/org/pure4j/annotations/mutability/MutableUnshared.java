
package org.pure4j.annotations.mutability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeQualifier;

/**
 * See: <a href="http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/MutableUnshared.html">Online Spec</a>
 * @author robmoffat
 *
 */
@TypeQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf(UnknownMutability.class)
public @interface MutableUnshared {

}
