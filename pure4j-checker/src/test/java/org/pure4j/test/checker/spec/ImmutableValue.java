package org.pure4j.test.checker.spec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pure4j.exception.ClassExpectingPureMethod;
import org.pure4j.immutable.RuntimeImmutabilityChecker;
import org.pure4j.test.checker.spec.generics.holder_immutable.GenericHolderImm;
import org.pure4j.test.checker.spec.generics.holder_immutable.SomeImmutable;
import org.pure4j.test.checker.spec.generics.holder_mutable.GenericHolderMutable;
import org.pure4j.test.checker.spec.generics.holder_mutable.SomeMutableUnsharedInterface;
import org.pure4j.test.checker.spec.generics.holder_object.GenericHolderX;
import org.pure4j.test.checker.spec.immutable.array.HasPrivateArray;
import org.pure4j.test.checker.spec.immutable.broken_extend.SomeValueObjectBrokenExtend;
import org.pure4j.test.checker.spec.immutable.calling_impure.SomeValueObjectWithImpures;
import org.pure4j.test.checker.spec.immutable.composition.CompositionalObject;
import org.pure4j.test.checker.spec.immutable.equals.ConfusingEquals;
import org.pure4j.test.checker.spec.immutable.equals.ConfusingEquals2;
import org.pure4j.test.checker.spec.immutable.exception.ExceptionGenerating;
import org.pure4j.test.checker.spec.immutable.forced2.ArrayBeingUsed;
import org.pure4j.test.checker.spec.immutable.good.SomeGoodValueObject;
import org.pure4j.test.checker.spec.immutable.interface_ann.SomeValueObject1;
import org.pure4j.test.checker.spec.immutable.missing_final.AbstractBrokenObject;
import org.pure4j.test.checker.spec.immutable.missing_final.BrokenValueObject;
import org.pure4j.test.checker.spec.immutable.mutable_field.NotImmutable;
import org.pure4j.test.checker.spec.immutable.no_to_string.NoToString;
import org.pure4j.test.checker.spec.immutable.non_pure_param.NonPureParameter;
import org.pure4j.test.checker.spec.immutable.overriding.SomeClass1;
import org.pure4j.test.checker.spec.immutable.overriding.SomeClass2;
import org.pure4j.test.checker.spec.immutable.parent_class_a_library.SomeBeanLibraryBased;
import org.pure4j.test.checker.spec.immutable.private_method.DifferentMethodAccessors;
import org.pure4j.test.checker.spec.immutable.static_example.StaticExample;
import org.pure4j.test.checker.spec.immutable.using_super.UsingSuper;

@RunWith(ConcordionRunner.class)
public class ImmutableValue {

	@Test
	public void arrayNotImmutable() {
		Helper.check(1, HasPrivateArray.class);
	}
	
	@Test
	public void returnTypeAnything() {
		Helper.check(0, ArrayBeingUsed.class);
	}

	@Test
	public void fieldsMustBeFinal() {
		Helper.check(0, AbstractBrokenObject.class, BrokenValueObject.class);	// missing final
	}

	@Test
	public void annotationIsInherited() {
		Helper.check(1, SomeValueObject1.class);
	}
	
	@Test 
	public void annotationFromSuper() {
		Helper.check(0, SomeValueObjectBrokenExtend.class);
	}
	

	@Test
	public void methodsMustBePure() {
		Helper.check(1, SomeValueObjectWithImpures.class);
	}

	@Test
	public void fieldsMustBeImmutable() {
		Helper.check(0, NotImmutable.class);
	}

	@Test
	public void genericParamsMustBeImmutable() {
		Helper.check(0, GenericHolderImm.class, SomeImmutable.class);
		Helper.check(0, GenericHolderMutable.class, SomeMutableUnsharedInterface.class);
		Helper.check(0, GenericHolderX.class);
	}

	@Test
	public void parametersMustBeImmutable() {
		Helper.check(0, NonPureParameter.class);
	}

	@Test
	public void overriddenMethodPurityNotChecked() {
		Helper.check(2, SomeClass1.class, SomeClass2.class);
		Helper.check(1, UsingSuper.class);
	}

	@Test
	public void staticMethodsNotPure() {
		Helper.check(0, StaticExample.class);
	}

	@Test
	public void privateMethodsNeedNotBeInterfacePure() {
		Helper.check(1, DifferentMethodAccessors.class);
	}

	@Test
	public void stringsAreImmutable() {
		Helper.check(0, SomeGoodValueObject.class);
	}

	@Test
	public void primitivesAreImmutable() {
		Helper.check(0, SomeGoodValueObject.class);
	}

	@Test
	public void immutableValuesCanBeParameters() {
		Helper.check(0, CompositionalObject.class);
	}

	@Test
	public void bigDecimalIsImmutable() {
		Assert.assertTrue(RuntimeImmutabilityChecker.isClassImmutable(BigDecimal.class));
	}

	@Test
	public void currencyIsImmutable() {
		Assert.assertTrue(RuntimeImmutabilityChecker.isClassImmutable(Currency.class));
	}

	@Test
	public void exceptionsTreatedImmutable() {
		Helper.check(0, ExceptionGenerating.class);
	}

	@Test
	public void collectionsNotImmutable() {
		Assert.assertFalse(RuntimeImmutabilityChecker.isClassImmutable(ArrayList.class));
		Assert.assertFalse(RuntimeImmutabilityChecker.isClassImmutable(HashSet.class));
	}

	@Test
	public void hashCodeAndToStringMustBeImplemented() {
		Helper.expects(ClassExpectingPureMethod.class).thenCheck(1, 0, NoToString.class);
	}

	@Test
	public void abstractImmutableValueImplementsHashCodeAndEquals() {
		Helper.check(0, 1, SomeBeanLibraryBased.class);
	}
	
	@Test
	public void usingLibrary() {
		Helper.check(0, 1, SomeBeanLibraryBased.class);
	}

	@Test
	public void equalsNeedNotBeImplemented() {
		Helper.check(2, ConfusingEquals.class, ConfusingEquals2.class);
	}

}
