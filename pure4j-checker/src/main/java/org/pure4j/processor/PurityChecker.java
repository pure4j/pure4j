package org.pure4j.processor;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PureInterface;
import org.pure4j.annotations.pure.PurityType;
import org.pure4j.exception.ClassHasConflictingAnnotationsException;
import org.pure4j.exception.ImpureCodeCallingPureCodeWithoutInterfacePurity;
import org.pure4j.exception.MemberCantBeHydratedException;
import org.pure4j.immutable.RuntimeImmutabilityChecker;
import org.pure4j.model.AnnotatedElementHandle;
import org.pure4j.model.CallHandle;
import org.pure4j.model.DeclarationHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.impl.AnnotationHandle;
import org.pure4j.model.impl.ClassHandle;
import org.pure4j.model.impl.ClassInitHandle;
import org.pure4j.model.impl.ConstructorDeclarationHandle;
import org.pure4j.model.impl.MethodDeclarationHandle;
import org.pure4j.model.impl.StackArgumentsMethodCallHandle;
import org.pure4j.processor.PureChecklistHandler.PureMethod;

public class PurityChecker implements Rule {
	
		
	private ClassAnnotationCache immutables = new ImmutableValueClassHandler();
	private ClassAnnotationCache mutableUnshared = new MutableUnsharedClassHandler(immutables);
	private PureChecklistHandler pureChecklist;
	private ClassLoader cl;

	public PurityChecker(ClassLoader cl) {
		this.cl = cl;
		pureChecklist = new PureChecklistHandler(cl, immutables, mutableUnshared, true, true);
	}
	
	public PurityChecker(ClassLoader cl, boolean intf, boolean impl) {
		this.cl = cl;
		pureChecklist = new PureChecklistHandler(cl, immutables, mutableUnshared, intf, impl);
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
		identifyImpureCallsToPureImplementations(pm, cb);
		identifyImpureImplementations(pm, cb);
		outputPureMethodList(cb, pm);	
	}
	
	private void identifyImpureCallsToPureImplementations(ProjectModel pm, Callback cb) {
		for (DeclarationHandle declaration : pm.getAllDeclaredMethods()) {
			boolean pure = false;
			
			try {
				pure = pureChecklist.isMarkedPure(declaration, cb, pm);
			} catch (MemberCantBeHydratedException e) {
			}
			
			if (!pure) {
				for (CallHandle called : pm.getCalls(declaration)) {
					PureMethod pureMethod = pureChecklist.getElementFor(called);
					if (pureMethod != null) {
						if (true == pureMethod.checkImplementationPurity(cb, pm)) {
							if (false == pureMethod.checkInterfacePurity(cb, pm)) {
								cb.registerError(new ImpureCodeCallingPureCodeWithoutInterfacePurity(declaration, called));
							}
						} 
					}
				}
			}
		}
	}

	public Set<String> identifyImpureImplementations(ProjectModel pm, Callback cb) {
		return new ImpurityCascader().getPurityViolations(pureChecklist, pm, cb, cl);
	}
	
	public void cascadeImpurities(ProjectModel pm, Callback cb) {
		//for
		
	}
	
	public void outputPureMethodList(Callback cb, ProjectModel pm) {
		cb.send("Pure Methods");
		cb.send("------------");
		for (PureMethod pureMethod : pureChecklist.getMethodList()) {
			if (pm.getAllClasses().contains(pureMethod.declaration.getClassName())) {
				cb.registerPure(pureMethod.declaration.toString(), pureMethod.checkInterfacePurity(cb, pm), pureMethod.checkImplementationPurity(cb, pm));
			}
		}
	}

	private void addMethodsFromImmutableValueClassToPureList(ProjectModel pm, Callback cb) {
		for (String className : pm.getAllClasses()) {
			ClassHandle cl = pm.getClassHandle(className);
			if (immutables.classIsMarked(cl, cb, pm)) {
				ClassHandle immutableClass = pm.getClassHandle(className);
				if (immutableClass.isConcrete() && (!RuntimeImmutabilityChecker.INBUILT_IMMUTABLE_CLASSES.contains(immutableClass.getClassName()))) {
					immutables.doClassChecks(immutableClass, cb, pm);
					cb.send("@ImmutableValue: "+immutableClass);
					addMethodsFromClassToPureList(immutableClass, cb, pm, true, false); 
				}
			}
		}
	}

	private void addMethodsFromMutableUnsharedToPureList(ProjectModel pm, Callback cb) {
		for (String className : pm.getAllClasses()) {
			ClassHandle cl = pm.getClassHandle(className);
			if (mutableUnshared.classIsMarked(cl, cb, pm)) {
				if (immutables.classIsMarked(cl, cb, pm)) {
					cb.registerError(new ClassHasConflictingAnnotationsException(cl));
				} else {
					ClassHandle pureClass = pm.getClassHandle(className);
					if (pureClass.isConcrete()) {
						mutableUnshared.doClassChecks(pureClass, cb, pm);
						cb.send("@MutableUnshared: "+pureClass);
						addMethodsFromClassToPureList(pureClass, cb, pm, false, false);
					}
				}
			}
		}
	}
	
	public void addMethodsFromClassToPureList(ClassHandle pureClass, Callback cb, ProjectModel pm, boolean includeObject, boolean includeStatics) {
		cb.send(pureClass.toString()+" methods: ");
		Set<String> overrides = new LinkedHashSet<String>();
		ClassHandle in = pureClass;
		
		while (includeObject ? (in != null) : ((!in.isObject()) && (in != null))) {
			String className = in.getClassName();
			if (pm.getAllClasses().contains(className)) {
				for (DeclarationHandle mh : pm.getDeclaredMethods(className)) {
					if (mh.getName().equals("<clinit>")) {
						// handle purity of class initialization
						registerMethodWithCorrectEnforcement(pureClass, cb, (ClassInitHandle) mh, pm); 
					}
				}
			}
			Set<String> implementations = new LinkedHashSet<String>();
			for (ConstructorDeclarationHandle ch : in.getDeclaredConstructors()) {
				registerMethodWithCorrectEnforcement(pureClass, cb, ch, pm);
			}
			
			for (MethodDeclarationHandle mh : in.getDeclaredMethods()) {
				if (mh.isStatic() || includeStatics) {
					String signature = mh.getSignature();
					boolean overridden = overrides.contains(signature);
					boolean calledByThisClass = calledWithin(pm.getCalledBy(mh), pureClass, pm, mh);
					if ((!overridden) || (calledByThisClass)) {
						registerMethodWithCorrectEnforcement(pureClass, cb, mh, pm);
						implementations.add(signature);
					}
				}
			}
			
			overrides.addAll(implementations);
			in = pm.getClassHandle(in.getSuperclass());
		}
	}

	protected void registerMethodWithCorrectEnforcement(ClassHandle pureClass, Callback cb, DeclarationHandle ch, ProjectModel pm) {
		Enforcement impl = getImplementationEnforcement(ch);
		Enforcement intf = getInterfaceEnforcement(ch);
		PurityType ret = getPurityType(cb, pureClass, pm);
		pureChecklist.addMethod(ch, impl, intf, ret, pureClass, cb);
	}

	protected Enforcement getImplementationEnforcement(DeclarationHandle mh) {
		AnnotationHandle p = mh.getAnnotation(Pure.class);
		Enforcement e = p == null ? Enforcement.CHECKED : (Enforcement) p.getField("value");
		return e;
	}

	/**
	 * Even if a class overrides a method, it can still use super to call it.
	 */
	private boolean calledWithin(Set<DeclarationHandle> calledBy, ClassHandle pureClass, ProjectModel pm, MethodDeclarationHandle callTo) {
		for (DeclarationHandle memberHandle : calledBy) {
			if (calledWithin(memberHandle, pureClass, pm)) {
				for (CallHandle call : pm.getCalls(memberHandle)) {
					if (call.equals(callTo)) {
						// is it a call to "this"?
						if (call instanceof StackArgumentsMethodCallHandle) {
							if (((StackArgumentsMethodCallHandle) call).getLocalVariables().contains(0)) {
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private boolean calledWithin(DeclarationHandle calledBy, ClassHandle pureClass, ProjectModel pm) {
		if ((!pureClass.isObject()) && (pureClass != null)) {
			return (calledBy.getClassName().equals(pureClass.getClassName())) || calledWithin(calledBy, pm.getClassHandle(pureClass.getSuperclass()), pm);
		}
		
		return false;
	}

	public static boolean isConcrete(Class<?> someClass) {
		return !Modifier.isAbstract(someClass.getModifiers()) && !Modifier.isInterface(someClass.getModifiers());
	}	

	private void addPureMethodsToPureList(ProjectModel pm, Callback cb) {
		cb.send("@Pure methods:");
		for (AnnotatedElementHandle handle : pm.getMembersWithAnnotation(getInternalName(Pure.class))) {
			if (handle instanceof DeclarationHandle) {
				String className = ((DeclarationHandle) handle).getClassName();
				ClassHandle class1 = pm.getClassHandle(className);
				registerMethodWithCorrectEnforcement(class1, cb, (DeclarationHandle) handle, pm);
			}
		}
	}

	protected PurityType getPurityType(Callback cb, ClassHandle class1, ProjectModel pm) {
		boolean mutable = false;
		if (class1.isAnonymousInner()) {
			mutableUnshared.addClass(class1);
			mutable = true;
		} else if (mutableUnshared.classIsMarked(class1, cb, pm)) {
			mutable = true;
		}
		if (mutable) {
			return PurityType.MUTABLE_UNSHARED;
		} else {
			return PurityType.IMMUTABLE_VALUE;
		}
	}

	protected Enforcement getInterfaceEnforcement(DeclarationHandle handle) {
		AnnotationHandle pp = handle.getAnnotation(PureInterface.class);
		Enforcement intf = pp == null ? Enforcement.CHECKED : (Enforcement) pp.getField("value");
		return intf;
	}

	public static String getInternalName(Class<?> in) {
		return org.pure4j.model.Type.getInternalName(in);
	}
}
