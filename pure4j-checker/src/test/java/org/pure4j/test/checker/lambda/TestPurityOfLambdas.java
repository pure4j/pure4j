package org.pure4j.test.checker.lambda;

import org.junit.Test;
import org.pure4j.test.checker.Helper;
import org.pure4j.test.checker.lambda.filter.PureFilter;
import org.pure4j.test.checker.lambda.map.PureMap;
import org.pure4j.test.checker.lambda.reduce.PureReduce;

public class TestPurityOfLambdas {

	@Test 
	public void testPureFilterPurity() {
		Helper.check(0, PureFilter.class);
	}
	
	@Test 
	public void testPureMapPurity() {
		Helper.check(0, PureMap.class);
	}
	
	@Test 
	public void testPureReducePurity() {
		Helper.check(0, PureReduce.class);
	}
}
