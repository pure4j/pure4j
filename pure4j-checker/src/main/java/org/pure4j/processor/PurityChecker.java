package org.pure4j.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.model.CallHandle;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

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
		addPureMethodsToPureList(pm, cb);
		addMethodsFromImmutableValueClassToPureList(pm, cb);
		addMethodsFromPureClassToPureList(pm, cb);
		pureChecklist.doPureMethodChecks(cb, pm);
		identifyImpureImplementations(pm, cb);
		outputPureMethodList(cb, pm);	
	}
	
	public Set<String> identifyImpureImplementations(ProjectModel pm, Callback cb) {
		return new InterfacePurityViolations().getPurityViolations(pureChecklist, pm, cb, cl);
	}
	
	public void cascadeImpurities(ProjectModel pm, Callback cb) {
		//for
		
	}
	
	public void outputPureMethodList(Callback cb, ProjectModel pm) {
		for (PureMethod pureMethod : pureChecklist.getMethodList()) {
			if ((pureMethod.checkImplementationPurity(cb, pm))) {
				if (pm.getAllClasses().contains(pureMethod.declaration.getDeclaringClass())) {
					cb.registerPure(pureMethod.declaration.toString());
				}
			}
		}
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
			for (Constructor<?> c : in.getDeclaredConstructors()) {
				ConstructorHandle ch = new ConstructorHandle(c);
				Pure p = c.getAnnotation(Pure.class);
				Enforcement e = p == null ? Enforcement.CHECKED : p.value();
				pureChecklist.addMethod(ch, e, pureClass, cb);
			}
			
			for (Method m : in.getDeclaredMethods()) {
				MethodHandle mh = new MethodHandle(m);
				Pure p = mh.getAnnotation(cl, Pure.class);
				Enforcement e = p == null ? Enforcement.CHECKED : p.value();
				if (!isStaticMethod(m)) {
					String signature = mh.getSignature();
					boolean overridden = addedSoFar.contains(signature);
					boolean calledByThisClass = false; //calledWithin(pm.getCalledBy(mh), pureClass);
					if ((!overridden) || (calledByThisClass)) {
						pureChecklist.addMethod(mh, e, pureClass, cb);
						addedSoFar.add(signature);
					}
				}
			}
			
			in = in.getSuperclass();
		}
	}

	protected boolean isStaticMethod(Method m) {
		return Modifier.isStatic(m.getModifiers());
	}

	/**
	 * Even if a class overrides a method, it can still use super to call it.
	 */
	private boolean calledWithin(Set<MemberHandle> calledBy, Class<?> pureClass) {
		for (MemberHandle memberHandle : calledBy) {
			if (calledWithin(memberHandle, pureClass)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean calledWithin(MemberHandle calledBy, Class<?> pureClass) {
		if ((pureClass != Object.class) && (pureClass != null)) {
			return (calledBy.getDeclaringClass(cl) == pureClass) || calledWithin(calledBy, pureClass.getSuperclass());
		}
		
		return false;
	}

	public static boolean isConcrete(Class<?> someClass) {
		return !Modifier.isAbstract(someClass.getModifiers()) && !Modifier.isInterface(someClass.getModifiers());
	}	

	private Class<?> hydrate(String className) {
		return new ClassHandle(className).hydrate(cl);
	}

	private void addPureMethodsToPureList(ProjectModel pm, Callback cb) {
		for (MemberHandle handle : pm.getMembersWithAnnotation(getInternalName(Pure.class))) {
			if (handle instanceof CallHandle) {
				Class<?> class1 = ((CallHandle)handle).getDeclaringClass(cl);
				Pure p = ((CallHandle)handle).getAnnotation(cl, Pure.class);
				if (p.value() != Enforcement.NOT_PURE) {
					pureChecklist.addMethod((CallHandle) handle, p.value(), class1, cb);
				}
			}
		}
	}

	public static String getInternalName(Class<?> in) {
		return org.pure4j.model.Type.getInternalName(in);
	}
}
