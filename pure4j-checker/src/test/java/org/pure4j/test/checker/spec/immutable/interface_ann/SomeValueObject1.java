package org.pure4j.test.checker.spec.immutable.interface_ann;

import org.pure4j.Pure4J;
import org.pure4j.test.ShouldBePure;

public class SomeValueObject1 implements SomeInterface {

	final boolean rob = false;
	
	@ShouldBePure
	@Override
	public int hashCode() {
		return Pure4J.hashCode(rob);
	}

	@ShouldBePure
	@Override
	public String toString() {
		return Pure4J.toString(this, rob);
	}
	
	
}
