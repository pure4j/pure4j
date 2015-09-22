package org.pure4j.checker.basic.corners.both_annotations;

import java.io.InputStream;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.ClassExpectingPureMethod;
import org.pure4j.exception.FieldNotFinalException;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.exception.PureMethodCallsImpureException;

@ImmutableValue
public interface BrokenValueObject {

}
