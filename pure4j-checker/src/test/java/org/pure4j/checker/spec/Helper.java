package org.pure4j.checker.spec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.checker.basic.support.CausesError;
import org.pure4j.checker.basic.support.ShouldBePure;
import org.pure4j.exception.Pure4JException;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.ClassFileModelBuilder;
import org.pure4j.processor.PurityChecker;
import org.pure4j.processor.SpringProjectModelFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

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
		new Helper().thenCheck(defaultConstructors, classes);
	}

	public boolean thenCheck(int defaultConstructors, Class<?>... classes) {
		try {
			final Set<String> pures = new LinkedHashSet<String>();
			final Map<Class<? extends Pure4JException>, Integer> errorSet = new HashMap<Class<? extends Pure4JException>, Integer>();

			Callback cb = new Callback() {

				@Override
				public void send(String s) {
					System.out.println(s);
					System.out.flush();
				}

				@Override
				public void registerError(Pure4JException e) {
					Integer count = errorSet.get(e.getClass());
					if (count == null) {
						count = 1;
					} else {
						count = count + 1;
					}

					errorSet.put(e.getClass(), count);
					System.err.println(e.getClass() + ": " + e.getMessage());
					System.err.flush();
				}

				@Override
				public void registerPure(String signature) {
					pures.add(signature);
				}
			};

			String packageName = this.getClass().getPackage().getName();
			ProjectModel pm = createModel(classes, packageName);
			PurityChecker checker = new PurityChecker(this.getClass().getClassLoader());
			checker.checkModel(pm, cb);

			System.out.println("----- PURES ---- ");
			for (String string : pures) {
				System.out.println(string);
			}

			System.out.flush();

			String pureAnn = org.pure4j.model.Type.getInternalName(ShouldBePure.class);
			for (MemberHandle mh : pm.getMembersWithAnnotation(pureAnn)) {
				String toStringmh = mh.toString();
				if (!pures.remove(toStringmh)) {
					System.err.println("Couldn't find expected pure: " + toStringmh);
				}
			}

			System.out.println("----- EXTRA PURES ---- ");
			for (String string : pures) {
				System.out.println(string);
			}

			Assert.assertEquals(defaultConstructors, pures.size());

			String errorAnn = org.pure4j.model.Type.getInternalName(CausesError.class);

			StringBuilder fail = new StringBuilder();

			for (MemberHandle mh : pm.getMembersWithAnnotation(errorAnn)) {
				CausesError vals = mh.getAnnotation(this.getClass().getClassLoader(), CausesError.class);
				for (Class<? extends Pure4JException> ex : vals.value()) {
					countException(errorSet, fail, ex);
				}
			}

			for (Class<?> ex : otherExceptions) {
				countException(errorSet, fail, ex);
			}

			otherExceptions = new Class[] {};

			for (Class<? extends Exception> e : errorSet.keySet()) {
				fail.append("Checker logged exception: " + e);
			}

			Assert.assertTrue(fail.toString(), fail.length() == 0);

			return true;
		} catch (IOException e) {
			throw new RuntimeException("Problem:", e);
		}

	}

	@Pure(Enforcement.NOT_PURE)
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

	@Pure(Enforcement.FORCE)
	public void assertEquals(Object exp, Object act) {
		if (!exp.equals(act)) {
			throw new RuntimeException("Was expecting equality: " + exp + " and " + act);
		}
	}

	@Pure(Enforcement.FORCE)
	public void log(String s) {
		System.out.println(s);
	}

	protected ProjectModel createModel(Class<?>[] classes, String packageStem) throws IOException {
		ClassFileModelBuilder cfmb = new ClassFileModelBuilder(false);
		ClassLoader cl = this.getClass().getClassLoader();
		DefaultResourceLoader drl = new DefaultResourceLoader(cl);
		Set<Resource> resources = new HashSet<Resource>();

		for (Class<?> c : classes) {
			visitAllOf(c, drl, cfmb, packageStem, new HashSet<Class<?>>(), resources);
		}

		for (Resource resource : resources) {
			cfmb.visit(resource);
		}

		ProjectModel pm = cfmb.getModel();
		return pm;
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
