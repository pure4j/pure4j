package org.pure4j.annotations.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.ImplicitFor;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeQualifier;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.unknown.Mutable;

import com.sun.source.tree.Tree.Kind;

/**
 * See: <a href="http://robmoffat.github.io/pure4j/concordion/org/pure4j/test/checker/spec/ImmutableValue.html">Online Spec</a>
 *
 * 
 * 
 * @author robmoffat
 *
 */
@TypeQualifier
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.TYPE})
@ImplicitFor(trees={Kind.STRING_LITERAL, Kind.FLOAT_LITERAL, Kind.INT_LITERAL, Kind.BOOLEAN_LITERAL, Kind.DOUBLE_LITERAL, Kind.NULL_LITERAL})
@SubtypeOf({Mutable.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImmutableValue {

}
