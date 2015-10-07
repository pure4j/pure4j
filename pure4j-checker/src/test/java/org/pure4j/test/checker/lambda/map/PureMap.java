package org.pure4j.test.checker.lambda.map;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PureCollectors;
import org.pure4j.test.checker.support.AbstractChecker;
import org.pure4j.test.checker.support.ShouldBePure;

public class PureMap extends AbstractChecker {

	@ShouldBePure
	@Pure
	public static ISeq<String> consumeBlah(ArraySeq<String> in) {
		ISeq<String> done = in.stream().map((a) -> "johhny "+a).collect(PureCollectors.toSeq());
		return done;
	}
	
	
	@Test
	public void usingAPure() {
		ArraySeq<String> someSeqIn = new ArraySeq<String>("john", "eats", "chips");
		ArraySeq<String> someSeqOut = new ArraySeq<String>("johhny john", "johhny eats", "johhny chips");
		
		Assert.assertEquals(someSeqOut , consumeBlah(someSeqIn));
	}
	
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
