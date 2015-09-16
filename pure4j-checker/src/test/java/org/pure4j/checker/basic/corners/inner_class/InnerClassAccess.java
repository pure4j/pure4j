package org.pure4j.checker.basic.corners.inner_class;

import java.io.IOException;

import org.junit.Test;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.AbstractChecker;
import org.pure4j.checker.basic.support.ShouldBePure;

@Pure
public class InnerClassAccess extends AbstractChecker {

	private String s = "blah";
	
	@Pure
	class InnerClass {
 
		@ShouldBePure
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
		
		@Pure
		class InnerInnerClass {
			
			@ShouldBePure
			public InnerInnerClass() {
			}
			
			
			@ShouldBePure
			public String callSomething2() {
				return InnerClassAccess.this.myPrivate();
			}
			
			@ShouldBePure
			public int callSomething3() {
				return doSomething();
			}
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
