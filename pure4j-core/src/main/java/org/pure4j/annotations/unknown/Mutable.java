package org.pure4j.annotations.unknown;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeQualifier;
import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.mutable.MutableUnshared;

@TypeQualifier
@InvisibleQualifier
@DefaultQualifierInHierarchy
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mutable {

}
