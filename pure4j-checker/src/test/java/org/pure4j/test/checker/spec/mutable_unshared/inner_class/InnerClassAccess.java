package org.pure4j.test.checker.spec.mutable_unshared.inner_class;

import org.pure4j.annotations.mutable.MutableUnshared;
import org.pure4j.exception.FieldTypeNotImmutableException;
import org.pure4j.test.CausesError;
import org.pure4j.test.ShouldBePure;

@MutableUnshared
public class InnerClassAccess {

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
}
