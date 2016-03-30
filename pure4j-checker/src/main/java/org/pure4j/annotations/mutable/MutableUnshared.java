
package org.pure4j.annotations.mutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeQualifier;
import org.pure4j.annotations.unknown.Mutable;

/**
 * See: <a href="http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/MutableUnshared.html">Online Spec</a>
 * @author robmoffat
 *
 */

@TypeQualifier
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.TYPE})
@SubtypeOf(Mutable.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MutableUnshared {

}
