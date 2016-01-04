package org.pure4j.test.processor;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.pure4j.processor.Pure4JAnnotationProcessor;

import com.google.testing.compile.JavaFileObjects;

public class QuickTestsOfCompiler {
	
	@Test
	public void testSomeTruth() {
		 assert_().about(javaSource())
		.that(JavaFileObjects.forSourceString("HelloWorld", "final class HelloWorld {}"))
		.compilesWithoutError();
	}
	
	@Test
	public void testWithAnnotationProcessor() throws IOException {
		assert_().about(javaSources())
		        .that(Arrays.asList(getFileContents(SimpleImmutableValueTest.class),
		        	getFileContents(SomeClassWithAPure.class)))
		        .processedWith(new Pure4JAnnotationProcessor())
		        .compilesWithoutError();
	}

	protected JavaFileObject getFileContents(Class<?> c) throws IOException {
		File f = new File("src/test/java/"+c.getName().replace(".", "/")+".java");
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		while (line != null) {
			sb.append(line);
			sb.append("\n");
			line = br.readLine();
		}
		br.close();
		return JavaFileObjects.forSourceString(c.getName(), sb.toString());
	}
}
