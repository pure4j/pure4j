package org.pure4j.checker.basic.mutable_unshared.return_type;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.checker.AbstractChecker;

public class CheckHandlingOfCovariantReturnType extends AbstractChecker {

	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1, 2);
	}
	
	
	@Test
	public void doSomeTest() {
		Class2 s = new Class2().process(6);
		System.out.println("Returned "+s);
		Object p = new Class2().process(88);
		System.out.println("Returned "+p);
	}
}
