package org.pure4j.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureParameters;
import org.pure4j.exception.ClassHasConflictingAnnotationsException;
import org.pure4j.model.CallHandle;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.StackArgumentsMethodCall;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PurityChecker implements Rule {
	
		
	private ClassAnnotationCache immutables = new ImmutableValueClassHandler();
	private ClassAnnotationCache mutableUnshared = new MutableUnsharedClassHandler(immutables);
	private PureChecklistHandler pureChecklist;
	private ClassLoader cl;

	public PurityChecker(ClassLoader cl) {
		this.cl = cl;
		pureChecklist = new PureChecklistHandler(cl, immutables, mutableUnshared);
		
	}
	
	@Override
	public void checkModel(ProjectModel pm, Callback cb) {
		cb.send("Method Scanning");
		cb.send("---------------");
		addPureMethodsToPureList(pm, cb);
		addMethodsFromImmutableValueClassToPureList(pm, cb);
		addMethodsFromMutableUnsharedToPureList(pm, cb);
		cb.send("Method Purity Testing");
		cb.send("---------------------");
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
		cb.send("Pure Methods");
		cb.send("------------");
		for (PureMethod pureMethod : pureChecklist.getMethodList()) {
			if ((pureMethod.checkPurity(cb, pm))) {
				if (pm.getAllClasses().contains(pureMethod.declaration.getDeclaringClass())) {
					cb.registerPure(pureMethod.declaration.toString());
				}
			}
		}
	}

	private void addMethodsFromImmutableValueClassToPureList(ProjectModel pm, Callback cb) {
		for (String className : pm.getAllClasses()) {
			Class<?> cl = hydrate(className);
			if (immutables.classIsMarked(cl, cb)) {
				Class<?> immutableClass = hydrate(className);
				if (isConcrete(immutableClass)) {
					immutables.doClassChecks(immutableClass, cb);
					cb.send("@ImmutableValue: "+immutableClass);
					addMethodsFromClassToPureList(immutableClass, cb, pm, true); 
				}
			}
		}
	}

	private void addMethodsFromMutableUnsharedToPureList(ProjectModel pm, Callback cb) {
		for (String className : pm.getAllClasses()) {
			Class<?> cl = hydrate(className);
			if (mutableUnshared.classIsMarked(cl, cb)) {
				if (immutables.classIsMarked(cl, cb)) {
					cb.registerError(new ClassHasConflictingAnnotationsException(cl));
				} else {
					Class<?> pureClass = hydrate(className);
					if (isConcrete(pureClass)) {
						mutableUnshared.doClassChecks(pureClass, cb);
						cb.send("@MutableUnshared: "+pureClass);
						addMethodsFromClassToPureList(pureClass, cb, pm, false);
					}
				}
			}
		}
	}
	
	public void addMethodsFromClassToPureList(Class<?> pureClass, Callback cb, ProjectModel pm, boolean includeObject) {
		Set<String> overrides = new LinkedHashSet<String>();
		Class<?> in = pureClass;
		
		while (includeObject ? (in != null) : (in != Object.class)) {
			Set<String> implementations = new LinkedHashSet<String>();
			for (Constructor<?> c : in.getDeclaredConstructors()) {
				ConstructorHandle ch = new ConstructorHandle(c);
				registerMethodWithCorrectEnforcement(pureClass, cb, ch);
			}
			
			for (Method m : in.getDeclaredMethods()) {
				MethodHandle mh = new MethodHandle(m);
				Pure p = mh.getAnnotation(cl, Pure.class);
				Enforcement e = p == null ? Enforcement.CHECKED : p.value();
				if (!isStaticMethod(m)) {
					String signature = mh.getSignature();
					boolean overridden = overrides.contains(signature);
					boolean calledByThisClass = calledWithin(pm.getCalledBy(mh), pureClass, pm, mh);
					if ((!overridden) || (calledByThisClass)) {
						registerMethodWithCorrectEnforcement(pureClass, cb, mh);
						implementations.add(signature);
					}
				}
			}
			
			overrides.addAll(implementations);
			in = in.getSuperclass();
		}
	}

	protected void registerMethodWithCorrectEnforcement(Class<?> pureClass, Callback cb, CallHandle ch) {
		Enforcement impl = getImplementationEnforcement(ch);
		Enforcement intf = getInterfaceEnforcement(ch);
		boolean ret = getReturnTypeEnforcement(cb, ch);
		pureChecklist.addMethod(ch, impl, intf, ret, pureClass, cb);
	}

	protected Enforcement getImplementationEnforcement(MemberHandle mh) {
		Pure p = mh.getAnnotation(cl, Pure.class);
		Enforcement e = p == null ? Enforcement.CHECKED : p.value();
		return e;
	}

	protected boolean isStaticMethod(Method m) {
		return Modifier.isStatic(m.getModifiers());
	}

	/**
	 * Even if a class overrides a method, it can still use super to call it.
	 */
	private boolean calledWithin(Set<MemberHandle> calledBy, Class<?> pureClass, ProjectModel pm, MethodHandle callTo) {
		for (MemberHandle memberHandle : calledBy) {
			if (calledWithin(memberHandle, pureClass)) {
				for (MemberHandle call : pm.getCalls(memberHandle)) {
					if (call.equals(callTo)) {
						// is it a call to "this"?
						if (call instanceof StackArgumentsMethodCall) {
							if (((StackArgumentsMethodCall) call).getLocalVariables().contains(0)) {
								return true;
							}
						}
					}
				}
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
		cb.send("@Pure methods:");
		for (MemberHandle handle : pm.getMembersWithAnnotation(getInternalName(Pure.class))) {
			if (handle instanceof CallHandle) {
				Class<?> class1 = ((CallHandle)handle).getDeclaringClass(cl);
				registerMethodWithCorrectEnforcement(class1, cb, (CallHandle) handle);
			}
		}
	}

	protected boolean getReturnTypeEnforcement(Callback cb, MemberHandle handle) {
		Class<?> class1 = handle.getDeclaringClass(cl);
		IgnoreImmutableTypeCheck iitc = ((CallHandle)handle).getAnnotation(cl, IgnoreImmutableTypeCheck.class);
		boolean checkReturnType = mutableUnshared.classIsMarked(class1, cb) && (iitc == null);
		return checkReturnType;
	}

	protected Enforcement getInterfaceEnforcement(MemberHandle handle) {
		PureParameters pp = ((CallHandle)handle).getAnnotation(cl, PureParameters.class);
		Enforcement intf = pp == null ? Enforcement.CHECKED : pp.value();
		return intf;
	}

	public static String getInternalName(Class<?> in) {
		return org.pure4j.model.Type.getInternalName(in);
	}
}
