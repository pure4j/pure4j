package org.pure4j.checker;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.PurityChecker;
import org.pure4j.processor.SpringProjectModelFactory;

public class AbstractChecker {
	
	@Pure(Enforcement.NOT_PURE)
	protected void checkThisPackage(Class<?> ofClass, int expectedErrorCount, int expectedPureCount) throws IOException {
		final Set<String> pures = new LinkedHashSet<String>();
		final Set<String> errorSet = new LinkedHashSet<String>();

		Callback cb = new Callback() {
			
			@Override
			public void send(String s) {
				System.out.println(s);
				System.out.flush();
			}
			
			@Override
			public void registerError(String s, Throwable optional) {
				errorSet.add(s);
				System.err.println(s);
				System.err.flush();
			}

			@Override
			public void registerPure(String signature) {
				pures.add(signature);
			}
		};

		
		SpringProjectModelFactory spmf = new SpringProjectModelFactory(new String[] { "target/classes", "target/test-classes" });
		spmf.setBasePackage(ofClass.getPackage().getName());
		spmf.setPattern("*.class");
		ProjectModel pm = spmf.createProjectModel(cb);
		PurityChecker checker = new PurityChecker(this.getClass().getClassLoader());
		checker.checkModel(pm, cb);
		
		System.out.println("----- PURES ---- ");
		for (String string : pures) {
			System.out.println(string);
		}
		
		System.out.flush();
		
		System.err.println("----- ERRORS ---- ");
		for (String string : errorSet) {
			System.err.println(string);
		}
		
		System.err.flush();
		
		Assert.assertEquals(expectedErrorCount, errorSet.size());
		Assert.assertEquals(expectedPureCount, pures.size());
	}
	
	@Pure(Enforcement.FORCE)
	public void assertEquals(Object exp, Object act) {
		if (!exp.equals(act)) {
			throw new RuntimeException("Was expecting equality: "+exp+" and "+act);
		}
	}
	
	@Pure(Enforcement.FORCE) 
	public void log(String s) {
		System.out.println(s);
	}
}
