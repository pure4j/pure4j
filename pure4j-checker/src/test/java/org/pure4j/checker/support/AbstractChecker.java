package org.pure4j.checker.support;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.Pure4JException;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.Callback;
import org.pure4j.processor.PurityChecker;
import org.pure4j.processor.SpringProjectModelFactory;

public class AbstractChecker {
	
	
	@Pure(Enforcement.FORCE)
	public AbstractChecker() {
		super();
	}

	
	@Pure(Enforcement.NOT_PURE)
	protected void checkThisPackage(Class<?> ofClass, int defaultConstructors, int covariants,  Class<?>... otherExceptions) throws IOException {
		checkThisPackage(ofClass, defaultConstructors+covariants, otherExceptions);
	}
	
	@Pure(Enforcement.NOT_PURE)
	protected void checkThisPackage(Class<?> ofClass, int defaultConstructors, Class<?>... otherExceptions) throws IOException {
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
				System.err.println(e.getClass()+": "+e.getMessage());
				System.err.flush();
			}

			@Override
			public void registerPure(String signature) {
				pures.add(signature);
			}
		};

		
		SpringProjectModelFactory spmf = new SpringProjectModelFactory(new String[] { "target/classes", "target/test-classes" }, true);
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
		
		String pureAnn = org.pure4j.model.Type.getInternalName(ShouldBePure.class);
		for (MemberHandle mh : pm.getMembersWithAnnotation(pureAnn)) {
			String toStringmh = mh.toString();
			if (!pures.remove(toStringmh)) {
				System.err.println("Couldn't find expected pure: "+toStringmh);
			}
		}
		
		System.out.println("----- EXTRA PURES ---- ");
		for (String string : pures) {
			System.out.println(string);
		}
		
		Assert.assertEquals(defaultConstructors, pures.size()); 
		
		String errorAnn = org.pure4j.model.Type.getInternalName(CausesError.class);
		
		boolean fail = false;
		
		for (MemberHandle mh : pm.getMembersWithAnnotation(errorAnn)) {
			CausesError vals =mh.getAnnotation(this.getClass().getClassLoader(), CausesError.class);
			for (Class<? extends Pure4JException> ex : vals.value()) {
				fail = countException(errorSet, fail, ex);
			}
		}
		
		for (Class<?> ex : otherExceptions) {
			fail = countException(errorSet, fail, ex);
		}
		
		for (Class<? extends Exception> e : errorSet.keySet()) {
			System.err.println("Checker logged exception: "+e);
			fail = true;
		}
		
		Assert.assertFalse(fail);
	}

	@Pure(Enforcement.NOT_PURE)
	@SuppressWarnings("unchecked")
	protected boolean countException(
			final Map<Class<? extends Pure4JException>, Integer> errorSet,
			boolean fail, Class<?> ex) {
		Integer count = errorSet.get(ex);
		if ((count == null) || (count == 0)) {
			System.err.println("Was expecting a "+ex.getName());
			fail = true;
		} else if (count > 1){
			count --;
			errorSet.put((Class<? extends Pure4JException>) ex, count);
		} else {
			errorSet.remove(ex);
		}
		return fail;
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
