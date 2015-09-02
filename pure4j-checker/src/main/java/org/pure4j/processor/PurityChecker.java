package org.pure4j.processor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.ProjectModel;

public class PurityChecker implements Rule {
	
		
	private ImmutableClassHandler immutables = new ImmutableClassHandler();
	private PureChecklistHandler pureChecklist;
	private ClassLoader cl;

	public PurityChecker(ClassLoader cl) {
		this.cl = cl;
		pureChecklist = new PureChecklistHandler(cl, immutables);
	}
	
	@Override
	public void checkModel(ProjectModel pm, Callback cb) {
		addPureMethodsToPureList(pm);
		addMethodsFromImmutableValueClassToPureList(pm, cb);
		addMethodsFromPureClassToPureList(pm, cb);
		
		pureChecklist.doPureMethodChecks(cb, pm);
		pureChecklist.outputPureMethodList(cb, pm);
	}

	private void addMethodsFromImmutableValueClassToPureList(ProjectModel pm, Callback cb) {
		for (String className : pm.getAllClasses()) {
			Class<?> cl = hydrate(className);
			if (immutables.classIsMarkedImmutable(cl, cb)) {
				Class<?> immutableClass = hydrate(className);
				if (isConcrete(immutableClass)) {
					immutables.doClassImmutabilityChecks(immutableClass, cb);
					addMethodsFromClassToPureList(immutableClass, cb, pm, true); 
				}
			}
		}
	}

	private void addMethodsFromPureClassToPureList(ProjectModel pm, Callback cb) {
		for (String className : pm.getAllClasses()) {
			Class<?> cl = hydrate(className);
			if (pureChecklist.isMarkedPure(cl, cb)) {
				Class<?> pureClass = hydrate(className);
				if (isConcrete(pureClass)) {
					addMethodsFromClassToPureList(pureClass, cb, pm, false);
				}
			}
		}
	}
	
	public void addMethodsFromClassToPureList(Class<?> pureClass, Callback cb, ProjectModel pm, boolean includeObject) {
		Set<String> addedSoFar = new LinkedHashSet<String>();
		Class<?> in = pureClass;
		while (includeObject ? (in != null) : (in != Object.class)) {
			for (Method m : in.getDeclaredMethods()) {
				MethodHandle mh = new MethodHandle(m);
				Pure p = m.getAnnotation(Pure.class);
				Enforcement e = p == null ? Enforcement.CHECKED : p.value();
				String signature = mh.getSignature();
				boolean overridden = addedSoFar.contains(signature);
				boolean calledBySomething = pm.getCalledBy(mh).size() > 0;
				if ((!overridden) || (calledBySomething)) {
					pureChecklist.addMethod(mh, e, pureClass);
					addedSoFar.add(signature);
				}
			}
			
			in = in.getSuperclass();
		}
	}

	public static boolean isConcrete(Class<?> someClass) {
		return !Modifier.isAbstract(someClass.getModifiers()) && !Modifier.isInterface(someClass.getModifiers());
	}	

	private Class<?> hydrate(String className) {
		return new ClassHandle(className).hydrate(cl);
	}

	private void addPureMethodsToPureList(ProjectModel pm) {
		for (MemberHandle handle : pm.getMembersWithAnnotation(getInternalName(Pure.class))) {
			if (handle instanceof MethodHandle) {
				Method m = ((MethodHandle)handle).hydrate(cl);
				Pure p = m.getAnnotation(Pure.class);
				if (p.value() != Enforcement.NOT_PURE) {
					pureChecklist.addMethod(handle, p.value(), m.getDeclaringClass());
				}
			}
		}
	}

	public static String getInternalName(Class<?> in) {
		return org.pure4j.model.Type.getInternalName(in);
	}
}
