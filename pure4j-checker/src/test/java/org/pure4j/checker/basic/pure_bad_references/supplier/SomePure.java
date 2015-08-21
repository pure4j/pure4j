package org.pure4j.checker.basic.pure_bad_references.supplier;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;

@Pure
public class SomePure {

	public void someOkCall() {
	}
	
	@Pure(Enforcement.NOT_PURE)
	public void someNotPureCall() {
	}
}
