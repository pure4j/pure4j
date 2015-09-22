package org.pure4j.checker.basic.mutable_unshared.inner_class;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.FieldTypeNotImmutableException;

@MutableUnshared
public class InnerClassAccess extends AbstractChecker {

	private String s = "blah";
	
	@MutableUnshared
	class InnerClass {
 
		@ShouldBePure
		@CausesError(FieldTypeNotImmutableException.class)	// inner classes don't work with MutableUnshared
		public InnerClass() {
			// this has a hidden parameter of the parent object, since this is a non-static inner class
		}
		
		@ShouldBePure
		public String callSomething() {
			return myPrivate();
		}
		
		@ShouldBePure
		public int doSomething() {
			return 5;
		}
	
	}
	
	@ShouldBePure
	private String myPrivate() {
		return s;
	}
	
	@Pure(Enforcement.NOT_PURE)
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 1);
	}
}
