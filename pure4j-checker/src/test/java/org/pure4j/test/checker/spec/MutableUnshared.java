package org.pure4j.test.checker.spec;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pure4j.test.checker.spec.mutable_unshared.bad_return.BadReturn;
import org.pure4j.test.checker.spec.mutable_unshared.classes.SomeMutableUnsharedClass;
import org.pure4j.test.checker.spec.mutable_unshared.inherit.BasePure;
import org.pure4j.test.checker.spec.mutable_unshared.inherit.InheritedPure;
import org.pure4j.test.checker.spec.mutable_unshared.inner_class.InnerClassAccess;
import org.pure4j.test.checker.spec.mutable_unshared.library.client.TestCanUsePureLibrary;
import org.pure4j.test.checker.spec.mutable_unshared.narrowing.PureNarrowingTest;
import org.pure4j.test.checker.spec.mutable_unshared.override.BadBase;
import org.pure4j.test.checker.spec.mutable_unshared.override.LeakyOverride;
import org.pure4j.test.checker.spec.mutable_unshared.override.PureClass;
import org.pure4j.test.checker.spec.mutable_unshared.return_this.ReturnThis;
import org.pure4j.test.checker.spec.mutable_unshared.runtime_narrow.BrokenRuntimeCheck;
import org.pure4j.test.checker.spec.mutable_unshared.runtime_narrow.NarrowAtRuntime;
import org.pure4j.test.checker.spec.mutable_unshared.setting_fields.FieldSetting;
import org.pure4j.test.checker.spec.mutable_unshared.static_call.TestCheckStatic;
import org.pure4j.test.checker.spec.mutable_unshared.visitor.VisitorExample;

@RunWith(ConcordionRunner.class)
public class MutableUnshared {
	
	@Test
	public void visitorExample() {
		Helper.check(1, VisitorExample.class);
	}

	@Test
	public void annotationInherited() {
		Helper.check(2, BasePure.class, InheritedPure.class);
	}

	@Test
	public void publicFieldsImmutable() {
		Helper.check(1, TestCheckStatic.class);
		Helper.check(1, SomeMutableUnsharedClass.class);
	}

	@Test
	public void publicParametersImmutable() {
		Helper.check(1,  PureNarrowingTest.class);
	}

	@Test
	public void immutableOnlyReturned() {
		Helper.check(1, BadReturn.class);
	}

	@Test
	public void parentClassNotImmutable() {
		Helper.check(1, InnerClassAccess.class);
		Helper.check(0, FieldSetting.class);
	}
	
	@Test
	public void thisAllowedAsReturn() {
		Helper.check(0, ReturnThis.class);
	}

	@Test
	public void covariantReturnTypeImmutable() {
		Helper.check(3 /* constructors */ + 2 /* covariants */, BadBase.class, LeakyOverride.class, PureClass.class);
	}

	@Test
	public void runtimeResultNarrowing() {
		Helper.check(1, NarrowAtRuntime.class);
		Helper.check(1, BrokenRuntimeCheck.class);
	}

	@Test
	public void libraryReference() {
		Helper.check(0, TestCanUsePureLibrary.class);
	}
	
}
