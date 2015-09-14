package org.pure4j.checker.basic.pure.static_call;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.PureMethodAccessesSharedFieldException;


public class TestCheckStatic extends AbstractChecker {
	
	@Pure
	public static class TestClass {
		
		private static int x_hidden = 5;
		
		public static int x_public = 6;
		
		@Pure
		@ShouldBePure
		public static int callOK() {
			return 5;
		}
		
		@Pure
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public static int callSharedStateBad() {
			return x_hidden;
		}
		
		@Pure
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public static int callNotOk() {
			return x_public;
		}
		
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public int memberNotOk() {
			return x_public;
		}
		
		@CausesError(PureMethodAccessesSharedFieldException.class)
		public int memberSharedStateNotOk() {
			return x_hidden;
		}
		
		@ShouldBePure
		public int shouldBeOK() {
			return callOK();
		}
	}

	@Pure(Enforcement.NOT_PURE)
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1);
	}
}
