package org.pure4j.checker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.PurityChecker;
import org.pure4j.processor.SpringProjectModelFactory;

public class AbstractChecker {

	int errors = 0;
	
	protected void checkThisPackage(Class<?> ofClass, int expectedErrorCount, int expectedPureCount) throws IOException {
		errors = 0;
		final List<String> pures = new ArrayList<String>();

		Callback cb = new Callback() {
			
			@Override
			public void send(String s) {
				System.out.println(s);
			}
			
			@Override
			public void registerError(String s, Throwable optional) {
				errors++;
				System.err.println(s);
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
		
		Assert.assertEquals(expectedErrorCount, errors);
		Assert.assertEquals(expectedPureCount, pures.size());
	}
}
