package org.pure4j.checker.basic.static_call;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;

@Pure
public class TestCheckStatic extends AbstractChecker {
	
	private static int x_hidden = 5;
	
	public static int x_public = 6;
	
	public static int callOK() {
		return 5;
	}
	
	public static int callSharedStateBad() {
		return x_hidden;
	}
	
	public static int callNotOk() {
		return x_public;
	}
	
	public int memberNotOk() {
		return x_public;
	}
	
	public int memberSharedStateNotOk() {
		return x_hidden;
	}
	
	public int shouldBeOK() {
		return callOK();
	}

	@Pure(Enforcement.NOT_PURE)
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 4, 2);
	}
}
