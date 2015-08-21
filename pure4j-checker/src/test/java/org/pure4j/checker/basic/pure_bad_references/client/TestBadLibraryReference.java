package org.pure4j.checker.basic.pure_bad_references.client;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.AbstractChecker;
import org.pure4j.checker.basic.pure_bad_references.supplier.SomePure;

public class TestBadLibraryReference extends AbstractChecker {

	@Pure
	public void doLibraryCall() {
		SomePure somePure = new SomePure();
		somePure.someOkCall();
		somePure.toString();
		somePure.someNotPureCall();
	}
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 2, 0);
	}
}