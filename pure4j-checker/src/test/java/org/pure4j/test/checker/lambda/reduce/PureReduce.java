package org.pure4j.test.checker.lambda.reduce;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.PersistentVector;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class PureReduce extends AbstractTest{

	@ShouldBePure
	@Pure
	public static String consumeBlah(PersistentVector<String> in) {
		String done = in.stream().reduce("", (a, b) -> a+b);
		return done;
	} 
	
	
	@Test
	public void usingAPure() {
		PersistentVector<String> someSeq = new PersistentVector<String>("john", "eats", "chips");
		Assert.assertEquals("johneatschips" , consumeBlah(someSeq));
	}

}
