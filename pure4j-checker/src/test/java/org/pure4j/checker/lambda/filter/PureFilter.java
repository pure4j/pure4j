package org.pure4j.checker.lambda.filter;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.support.AbstractChecker;
import org.pure4j.checker.support.ShouldBePure;
import org.pure4j.collections.ArraySeq;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PureCollectors;

public class PureFilter extends AbstractChecker {

	@ShouldBePure
	@Pure
	public ISeq<String> consumeBlah(ArraySeq<String> in) {
		ISeq<String> done = in.stream().filter((a) -> a.startsWith("e")).collect(PureCollectors.toSeq());
		return done;
	}
	
	
	@Test
	public void usingAPure() {
		ArraySeq<String> someSeq = new ArraySeq<String>("john", "eats", "chips");
		Assert.assertEquals(new ArraySeq<String>("eats") , consumeBlah(someSeq));
	}
	
	
	@Test
	public void checkThisPackage() throws IOException {
		checkThisPackage(this.getClass(), 0);
	}
}
