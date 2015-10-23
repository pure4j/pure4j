package org.pure4j.test.checker.lambda.map;

import junit.framework.Assert;

import org.junit.Test;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.collections.ISeq;
import org.pure4j.collections.PersistentList;
import org.pure4j.lambda.PureCollectors;
import org.pure4j.test.AbstractTest;
import org.pure4j.test.ShouldBePure;

public class PureMap extends AbstractTest {

	@ShouldBePure
	@Pure
	public static ISeq<String> consumeBlah(PersistentList<String> in) {
		ISeq<String> done = in.stream().map((a) -> "johhny "+a).collect(PureCollectors.toSeq());
		return done;
	}
	
	
	@Test
	public void usingAPure() {
		PersistentList<String> someSeqIn = new PersistentList<String>("john", "eats", "chips");
		PersistentList<String> someSeqOut = new PersistentList<String>("johhny john", "johhny eats", "johhny chips");
		
		Assert.assertEquals(someSeqOut , consumeBlah(someSeqIn));
	}

}
