package org.pure4j.checker;

import java.io.IOException;

import junit.framework.Assert;

import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.PurityChecker;
import org.pure4j.processor.SpringProjectModelFactory;

public class AbstractChecker {

	int errors = 0;
	
	public void checkThisPackage(Class<?> ofClass, int expectedErrorCount) throws IOException {
		errors = 0;

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
		};

		
		SpringProjectModelFactory spmf = new SpringProjectModelFactory(new String[] { "target/classes", "target/test-classes" });
		spmf.setBasePackage(ofClass.getPackage().getName());
		spmf.setPattern("*.class");
		ProjectModel pm = spmf.createProjectModel(cb);
		PurityChecker checker = new PurityChecker(this.getClass().getClassLoader());
		checker.checkModel(pm, cb);
		
		Assert.assertEquals(expectedErrorCount, errors);
	}
}
