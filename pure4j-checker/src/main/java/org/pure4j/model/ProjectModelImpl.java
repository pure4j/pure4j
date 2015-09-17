package org.pure4j.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ProjectModelImpl implements ProjectModel {

	private Map<Object, Boolean> finality = new HashMap<Object, Boolean>(100);
	private Map<MemberHandle, List<MemberHandle>> calls = new HashMap<MemberHandle, List<MemberHandle>>(100);
	private Map<MemberHandle, Set<MemberHandle>> calledBy = new HashMap<MemberHandle, Set<MemberHandle>>(100);
	private Map<String, Set<String>> annotatedClasses = new HashMap<String, Set<String>>(100);
	private Map<String, Set<MemberHandle>> annotatedMembers = new HashMap<String, Set<MemberHandle>>(100);
	private Map<String, Set<String>> subclasses = new HashMap<String, Set<String>>(100);
	private Map<String, Set<String>> dependsOnClasses = new HashMap<String, Set<String>>(100);
	private Map<String, Set<String>> dependedOnClasses = new HashMap<String, Set<String>>(100);
	private Map<PackageHandle, Set<PackageHandle>> dependsOnPackages = new HashMap<PackageHandle, Set<PackageHandle>>(100);
	private Map<PackageHandle, Set<PackageHandle>> dependedOnPackages = new HashMap<PackageHandle, Set<PackageHandle>>(100);
	private Map<String, Set<String>> packageContents = new HashMap<String, Set<String>>(100);
	private Map<String, Set<AnnotationHandle>> annotationReferences = new HashMap<String, Set<AnnotationHandle>>(100);
	private Map<CallHandle, Integer> opcodes = new HashMap<CallHandle, Integer>(100);

	private LinkedHashSet<String> classes = new LinkedHashSet<String>(100);

	public List<MemberHandle> getCalls(MemberHandle m) {
		return checkListGet(calls.get(m));
	}

	public Set<MemberHandle> getCalledBy(MemberHandle m) {
		return checkSetGet(calledBy.get(m));
	} 

	public Set<String> getClassesWithAnnotation(String annotationName) {
		return checkSetGet(annotatedClasses.get(annotationName));
	}

	public Set<MemberHandle> getMembersWithAnnotation(String annotationName) {
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

	public void addMemberAnnotation(String desc, MemberHandle mh) {
		checkSetAdd(desc, mh, annotatedMembers);
	}

	public void addCalls(MemberHandle desc, MemberHandle mh) {
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

	public void addPackageDependency(PackageHandle from, PackageHandle on) {
		if (!from.equals(on)) {
			checkSetAdd(from, on, dependsOnPackages);
			checkSetAdd(on, from, dependedOnPackages);
		}
	}

	public void addPackageClass(String packageName, String cl) {
		checkSetAdd(packageName, cl, packageContents);
	}

	public void addClass(String name) {
		classes.add(name);
	}

	public Set<String> getClassesInPackage(String packageName) {
		return checkSetGet(packageContents.get(packageName));
	}

	public boolean withinModel(String className) {
		return classes.contains(className);
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
	
	public Set<PackageHandle> getDependedOnPackages(PackageHandle packageName) {
		return checkSetGet(dependedOnPackages.get(packageName));
	}

	public Set<PackageHandle> getDependsOnPackages(PackageHandle packageName) {
		return checkSetGet(dependsOnPackages.get(packageName));
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
		return classes;
	}
	
	public void setFinal(Object thing, boolean finality) {
		this.finality.put(thing, finality);
	}

	@Override
	public int getOpcodes(CallHandle ch) {
		Integer out = opcodes.get(ch);
		return out == null ? 0 : out;
	}

	public void setOpcodes(CallHandle ch, int o) {
		opcodes.remove(ch);
		if (o != 0) {
			opcodes.put(ch, o);
		}
	}
}