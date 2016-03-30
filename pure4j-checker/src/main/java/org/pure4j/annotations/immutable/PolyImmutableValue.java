package org.pure4j.annotations.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.checkerframework.framework.qual.PolymorphicQualifier;
import org.checkerframework.framework.qual.TypeQualifier;


@TypeQualifier
@PolymorphicQualifier(ImmutableValue.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PolyImmutableValue {

}
