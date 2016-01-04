package org.pure4j.model.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.pure4j.model.AnnotatedElementHandle;
import org.pure4j.model.CallHandle;
import org.pure4j.model.CallInfo;
import org.pure4j.model.DeclarationHandle;
import org.pure4j.model.Handle;
import org.pure4j.model.ProjectModel;

public class ProjectModelImpl implements ProjectModel {

	private Map<Object, Boolean> finality = new HashMap<Object, Boolean>(100);
	private Map<DeclarationHandle, List<CallHandle>> calls = new HashMap<DeclarationHandle, List<CallHandle>>(100);
	private Map<CallHandle, Set<DeclarationHandle>> calledBy = new HashMap<CallHandle, Set<DeclarationHandle>>(100);
	private Map<String, Set<String>> annotatedClasses = new HashMap<String, Set<String>>(100);
	private Map<String, Set<AnnotatedElementHandle>> annotatedMembers = new HashMap<String, Set<AnnotatedElementHandle>>(100);
	private Map<String, Set<String>> subclasses = new HashMap<String, Set<String>>(100);
	private Map<String, Set<String>> dependsOnClasses = new HashMap<String, Set<String>>(100);
	private Map<String, Set<String>> dependedOnClasses = new HashMap<String, Set<String>>(100);
	private Map<String, Set<String>> packageContents = new HashMap<String, Set<String>>(100);
	private Map<String, Set<AnnotationHandle>> annotationReferences = new HashMap<String, Set<AnnotationHandle>>(100);
	private Map<Handle, CallInfo> opcodes = new HashMap<Handle, CallInfo>(100);
	private Set<DeclarationHandle> declaredMethods = new HashSet<DeclarationHandle>(1000);
	private Map<String, Set<DeclarationHandle>> declaredMethodsByClass = new HashMap<String, Set<DeclarationHandle>>(100);
	
	
	public void addDeclaredMethod(String className, DeclarationHandle mh) {
		declaredMethods.add(mh);
		checkSetAdd(className, mh, declaredMethodsByClass);
	}

	private LinkedHashMap<String, ClassHandle> classes = new LinkedHashMap<String, ClassHandle>(100);

	public List<CallHandle> getCalls(DeclarationHandle m) {
		return checkListGet(calls.get(m));
	}

	public Set<DeclarationHandle> getCalledBy(CallHandle m) {
		return checkSetGet(calledBy.get(m));
	} 

	public Set<String> getClassesWithAnnotation(String annotationName) {
		return checkSetGet(annotatedClasses.get(annotationName));
	}

	public Set<AnnotatedElementHandle> getMembersWithAnnotation(String annotationName) {
		return checkSetGet(annotatedMembers.get(annotationName));
	}

	public Set<String> getSubclasses(String className) {
		return checkSetGet(subclasses.get(className));
	}

	public void addSubclass(String superName, String name) {
		checkSetAdd(superName, name, subclasses);
	}

	private <X> Set<X> checkSetGet(Set<X> in) {
		if (in == null) {
			return Collections.emptySet();
		}

		return in;
	}
	
	private <X> List<X> checkListGet(List<X> in) {
		if (in == null) {
			return Collections.emptyList();
		}

		return in;
	}

	private <A, B> void checkSetAdd(A keyName, B value, Map<A, Set<B>> theMap) {
		Set<B> item = theMap.get(keyName);
		if (item == null) {
			item = new TreeSet<B>();
			theMap.put(keyName, item);
		}
		item.add(value);
	}
	
	private <A, B> void checkListAdd(A keyName, B value, Map<A, List<B>> theMap) {
		List<B> item = theMap.get(keyName);
		if (item == null) {
			item = new LinkedList<B>();
			theMap.put(keyName, item);
		}
		item.add(value);
	}

	public void addClassAnnotation(String desc, String className) {
		checkSetAdd(desc, className, annotatedClasses);
	}

	public void addMemberAnnotation(String desc, AnnotatedElementHandle mh) {
		checkSetAdd(desc, mh, annotatedMembers);
	}

	public void addCalls(DeclarationHandle desc, CallHandle mh) {
		if (!desc.equals(mh)) {
			checkListAdd(desc, mh, calls);
			checkSetAdd(mh, desc, calledBy);
		}
	}

	public void addClassDependency(String from, String on) {
		if (!from.equals(on)) {
			checkSetAdd(from, on, dependsOnClasses);
			checkSetAdd(on, from, dependedOnClasses);
		}
	}

	public void addPackageClass(String packageName, String cl) {
		checkSetAdd(packageName, cl, packageContents);
	}

	public void addClass(String name, ClassHandle ch) {
		classes.put(name, ch);
	}

	public Set<String> getClassesInPackage(String packageName) {
		return checkSetGet(packageContents.get(packageName));
	}

	public boolean withinModel(String className) {
		return classes.containsKey(className);
	}

	public int getClassCount() {
		return classes.size();
	}

	public Set<String> getDependedOnClasses(String className) {
		return checkSetGet(dependedOnClasses.get(className));
	}

	public Set<String> getDependsOnClasses(String className) {
		return checkSetGet(dependsOnClasses.get(className));
	}
	
	public Set<AnnotationHandle> getAnnotationReferences(String className) {
		return checkSetGet(annotationReferences.get(className));
	}

	public void addAnnotationReference(String referredClass, AnnotationHandle by) {
		checkSetAdd(referredClass, by, annotationReferences);
	}

	public boolean packageWithinModel(String packageName) {
		return packageContents.containsKey(packageName);
	}

	public Set<String> getAllClasses() {
		return classes.keySet();
	}
	
	public void setFinal(Object thing, boolean finality) {
		this.finality.put(thing, finality);
	}

	@Override
	public CallInfo getOpcodes(DeclarationHandle ch) {
		CallInfo out = opcodes.get(ch);
		return (out == null) ? CallInfo.NO_CALL : out;
	}

	public void setOpcodes(Handle ch, CallInfo o) {
		opcodes.put(ch, o);
	}

	@Override
	public Set<DeclarationHandle> getAllDeclaredMethods() {
		return declaredMethods;
	}

	@Override
	public Set<DeclarationHandle> getDeclaredMethods(String className) {
		return checkSetGet(declaredMethodsByClass.get(className));
	}

	@Override
	public ClassHandle getClassHandle(String className) {
		return className == null ? null : classes.get(className);
	}

	@Override
	public ClassHandle getClassHandle(CallHandle ch) {
		return getClassHandle(ch.getClassName());
	}

	
}