package org.pure4j.test.checker.spec;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pure4j.test.checker.spec.pure.bad_references.client.TestBadLibraryReference;
import org.pure4j.test.checker.spec.pure.callingnotinterfacepure.InterfaceNotPureOkCall;
import org.pure4j.test.checker.spec.pure.callingnotpure.TestIllegalObjectUse;
import org.pure4j.test.checker.spec.pure.construct.TestPureUse;
import org.pure4j.test.checker.spec.pure.forced1.ForceInterfacePurity;
import org.pure4j.test.checker.spec.pure.forced2.SomeForcedPure;
import org.pure4j.test.checker.spec.pure.forced3.SomeForcedImpure;
import org.pure4j.test.checker.spec.pure.forced4.ForceInterfaceNotPure;
import org.pure4j.test.checker.spec.pure.implementation_only.SomeImplementationOnlyPure;
import org.pure4j.test.checker.spec.pure.interface_not_pure.TestCheckCatchesNonImmutableValueArguments;
import org.pure4j.test.checker.spec.pure.math.PureMathsTests;
import org.pure4j.test.checker.spec.pure.runtime_checked.CheckParameterImmutability;
import org.pure4j.test.checker.spec.pure.runtime_unsupported.SimpleUnsupported;
import org.pure4j.test.checker.spec.pure.state.SomeNonPureObject;
import org.pure4j.test.checker.spec.pure.strings2.StringBuilding;

@RunWith(ConcordionRunner.class)
public class Pure {

	@Test
	public void onlyCallsOtherPures() {
		Helper.check(0, TestIllegalObjectUse.class);
	}
	
	@Test
	public void accessesOnlyImmutableState() {
		Helper.check(0, SomeNonPureObject.class);
	}
	
	@Test
	public void onlyAcceptsImmutableParameters() {
		Helper.check(0, TestCheckCatchesNonImmutableValueArguments.class);
	}
	
	@Test
	public void canAccessNonInterfacePure() {
		Helper.check(0, InterfaceNotPureOkCall.class);
	}
		
	@Test
	public void nonPureCodeAccessingOnlyImplementationPure() {
		Helper.check(0, SomeImplementationOnlyPure.class);
	}
	
	@Test
	public void canOverrideAndForcePure() {
		Helper.check(0, SomeForcedPure.class);
	}
	
	@Test
	public void canOverrideAndForceNotPure() {
		Helper.check(0, SomeForcedImpure.class);
	}
	
	@Test
	public void canOverrideAndForceInterfacePure() {
		Helper.check(0, ForceInterfacePurity.class);
	}
	
	@Test
	public void canOverrideAndForceInterfaceNotPure() {
		Helper.check(0, ForceInterfaceNotPure.class);
	}
	
	@Test
	public void runtimePurityChecking() {
		Helper.check(0, CheckParameterImmutability.class);
	}
	
	@Test
	public void unsupportedOperationCheck() {
		Helper.check(0, SimpleUnsupported.class);
	}
	
	@Test
	public void mathIsPure() {
		Helper.check(0, PureMathsTests.class);
	}
	
	@Test
	public void useOfObjects() {
		Helper.check(0, TestPureUse.class);
	}
	
	@Test
	public void staticUse() {
		Helper.check(0, TestPureUse.class);
	}
	
	@Test
	public void stringBuilding() {
		Helper.check(0, StringBuilding.class);
	}
	
	@Test
	public void testLibraryReference() {
		Helper.check(0, TestBadLibraryReference.class);
	}
}
