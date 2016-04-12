package org.pure4j.test.checker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.pure4j.annotations.pure.ForcePure;
import org.pure4j.annotations.pure.Impure;
import org.pure4j.exception.Pure4JException;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.Pure4JChecker;
import org.pure4j.test.CausesError;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import com.google.testing.compile.JavaFileObjects;

public final class Helper {

	Class<?>[] otherExceptions = new Class[] {};

	public static Helper expects(Class<?>... e) {
		return new Helper(e);
	}

	public Helper() {
	}

	public Helper(Class<?>... oe) {
		this.otherExceptions = oe;
	}
	
	public static void check(int defaultConstructors, Class<?>... classes) {
		new Helper().thenCheck(defaultConstructors, 0, classes);
	}

	public static void check(int defaultConstructors, int maxSynthetics, Class<?>... classes) {
		new Helper().thenCheck(defaultConstructors, maxSynthetics, classes);
	}
	
	public boolean thenCheck(int defaultConstructors, int maxSynthetics, Class<?>... classes) {
		DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
		try {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			CompilationTask task = compiler.getTask(
				null, compiler.getStandardFileManager(null, null, null), diagnosticCollector, 
				Arrays.asList(
						"-AprintErrorStack", 
						"-Ashowchecks"), null, getFileContents(classes));
			
			
			task.setProcessors(Collections.singleton(new Pure4JChecker()));
				
			task.call();
		}  catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("Problem:", e);
		}

		
		List<Diagnostic<?>> errors = new ArrayList<>();
		
		for (Diagnostic<?> d : diagnosticCollector.getDiagnostics()) {
			if (d.getKind() == Kind.ERROR) {
				errors.add(d);
			}
			
			System.out.println(d);
		}
		
		for (Class<?> class1 : classes) {
			for (Field f : class1.getDeclaredFields()) {
				CausesError ce = f.getAnnotation(CausesError.class);
				handleCausesAnnotation(errors, ce);
			}	
			
			for (Method m : class1.getDeclaredMethods()) {
				CausesError ce = m.getAnnotation(CausesError.class);
				handleCausesAnnotation(errors, ce);
			}
		}
		
		// remaining error codes
		Assert.assertTrue("Unexpected Errors: "+errors, errors.size() == 0);
		
		
		return true;
		
//
//			System.out.println("----- PURES ---- ");
//			for (String string : pures) {
//				System.out.println(string);
//			}
//
//			System.out.flush();
//
//			String pureAnn = org.pure4j.model.Type.getInternalName(ShouldBePure.class);
//			for (MemberHandle mh : pm.getMembersWithAnnotation(pureAnn)) {
//				String toStringmh = mh.toString();
//				if (!pures.remove(toStringmh)) {
//					System.err.println("Couldn't find expected pure: " + toStringmh);
//				}
//			}
//
//			System.out.println("----- EXTRA PURES ---- ");
//			for (String string : pures) {
//				System.out.println(string);
//			}
//
//			int synthetics = 0;
//			int constructors = 0;
//			for (String string : pures) {
//				if (string.contains("<")) {
//					constructors ++;
//				} else {
//					synthetics ++;
//				}
//			}
//			
//			Assert.assertEquals(defaultConstructors, constructors);
//			Assert.assertTrue(synthetics <= maxSynthetics);
//			
//
//			String errorAnn = org.pure4j.model.Type.getInternalName(CausesError.class);
//
//			StringBuilder fail = new StringBuilder();
//
//			for (MemberHandle mh : pm.getMembersWithAnnotation(errorAnn)) {
//				CausesError vals = mh.getAnnotation(this.getClass().getClassLoader(), CausesError.class);
//				for (Class<?> ex : vals.value()) {
//					countException(errorSet, fail, ex);
//				}
//			}
//
//			for (Class<?> ex : otherExceptions) {
//				countException(errorSet, fail, ex);
//			}
//
//			otherExceptions = new Class[] {};
//
//			for (Class<? extends Exception> e : errorSet.keySet()) {
//				fail.append("Checker logged exception: " + e);
//			}
//
//			Assert.assertTrue(fail.toString(), fail.length() == 0);
//
//			return true;
//		} catch (Throwable e) {
//			e.printStackTrace();
//			throw new RuntimeException("Problem:", e);
//		}

	}

	private void handleCausesAnnotation(List<Diagnostic<?>> errors, CausesError ce) {
		if ((ce != null) && (ce.code() != null)) {
			for(String err : ce.code()) {
				boolean found = extractError(errors, err);
				
				if (!found) {
					Assert.fail("Was expecting error code: "+err);
				}
			}
		}
	}

	private boolean extractError(List<Diagnostic<?>> errors, String err) {
		for (Iterator<Diagnostic<?>> iterator = errors.iterator(); iterator.hasNext();) {
			Diagnostic<?> diagnostic = iterator.next();
			if (diagnostic.toString().contains(err)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	private Iterable<? extends JavaFileObject> getFileContents(Class<?>[] classes) throws IOException {
		List<JavaFileObject> out = new ArrayList<JavaFileObject>();
		for (Class<?> c1 : classes) {
			File f = new File("src/test/java/"+c1.getName().replace(".", "/")+".java");
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			
			out.add(JavaFileObjects.forSourceString(c1.getName(), sb.toString()));
			br.close();
		}
		
		return out;
	}

	@Impure
	@SuppressWarnings("unchecked")
	protected void countException(final Map<Class<? extends Pure4JException>, Integer> errorSet, StringBuilder fail, Class<?> ex) {
		Integer count = errorSet.get(ex);
		if ((count == null) || (count == 0)) {
			fail.append("Was expecting a " + ex.getName());
		} else if (count > 1) {
			count--;
			errorSet.put((Class<? extends Pure4JException>) ex, count);
		} else {
			errorSet.remove(ex);
		}
	}

	@Impure
	public void assertEquals(Object exp, Object act) {
		if (!exp.equals(act)) {
			throw new RuntimeException("Was expecting equality: " + exp + " and " + act);
		}
	}

	@ForcePure
	public void log(String s) {
		System.out.println(s);
	}


	private void visitAllOf(Class<?> c, DefaultResourceLoader drl, ClassFileModelBuilder cfmb, String packageStem, Set<Class<?>> done, Set<Resource> resources) throws IOException {
		if ((c != Object.class) && (c != null) && (!done.contains(c))) {
			done.add(c);
			System.out.println("visiting: " + c);
			if (c.getName().startsWith(packageStem)) {
				resources.add(drl.getResource("classpath:/" + c.getName().replace(".", "/") + ".class"));
				for (Class<?> intf : c.getInterfaces()) {
					visitAllOf(intf, drl, cfmb, packageStem, done, resources);
				}

				for (Class<?> cl : c.getClasses()) {
					visitAllOf(cl, drl, cfmb, packageStem, done, resources);
				}

				visitAllOf(c.getSuperclass(), drl, cfmb, packageStem, done, resources);
			}
		}
	}

}
