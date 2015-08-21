package org.pure4j.processor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.immutable.HashHelp;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.FieldHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.ThisHandle;

@SuppressWarnings("unchecked")
public class PurityChecker implements Rule {
	
	public static final Set<?> INBUILT_IMMUTABLE_CLASSES = new HashSet<Object>();
	
	public static final boolean IGNORE_EXCEPTION_CONSTRUCTION = true;
	
	public static final boolean IGNORE_EQUALS_PARAMETER_PURITY = true;
	
	public static Set<String> PURE_LIST = PureList.loadPureLists();
	
	class PureMethod {
		
		public Class<?> usedFrom;
		public MemberHandle declaration;
		public Enforcement e;
		private Boolean pureInterface = null;
		private Boolean pureImplementation = null;
		private boolean silent;

		@Override
		public int hashCode() {
			return HashHelp.hashCode(usedFrom, declaration);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PureMethod other = (PureMethod) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (declaration == null) {
				if (other.declaration != null)
					return false;
			} else if (!declaration.equals(other.declaration))
				return false;
			if (usedFrom == null) {
				if (other.usedFrom != null)
					return false;
			} else if (!usedFrom.equals(other.usedFrom))
				return false;
			return true;
		}

		public PureMethod(Class<?> usedFrom, MemberHandle declaration, Enforcement e, boolean silent) {
			super();
			this.usedFrom = usedFrom;
			this.declaration = declaration;
			this.e = e;
			this.silent = silent;
			if (e==Enforcement.FORCE) {
				 pureImplementation = true;
				 pureInterface = true;
			} else if (e==Enforcement.NOT_PURE) {
				pureImplementation = false;
				pureInterface = false;
			}
		}

		@Override
		public String toString() {
			return "PureMethod [usedFrom=" + usedFrom + ", declaration="
					+ declaration + ", e=" + e + "]";
		}
		
		public boolean checkImplementationPurity(Callback cb, ProjectModel pm) {
			if (pureImplementation == null) {
				pureImplementation = true;
				for (MemberHandle mh: pm.getCalls(declaration)) {
					if (mh instanceof MethodHandle) {
						if (!isPureCall((MethodHandle) mh, cb)) {
							if (!silent) {
								cb.registerError(declaration+" is expected to be a pure method, but calls impure method "+mh, null);
							}
							pureImplementation = false;
						}
					} else if (mh instanceof ConstructorHandle) {
						if (!isPureCall((ConstructorHandle) mh, cb)) {
							if (!silent) {
								cb.registerError(declaration+" is expected to be a pure method, but calls impure constructor "+mh, null);
							}
							pureImplementation = false;
						}
					} else if (mh instanceof FieldHandle) {
						FieldHandle fieldHandle = (FieldHandle)mh;
						Field f = fieldHandle.hydrate(cl);
						if (!onCurrentObject(fieldHandle, f)) {
							if ((!Modifier.isFinal(f.getModifiers()))) {
								if (!silent) {
									cb.registerError(declaration+" is expected to be pure but accesses non-final field which isn't private/protected "+fieldHandle, null);
								}
								pureImplementation = false;
							}
							
							if (!typeIsImmutable(f.getGenericType(), cb)) {
								if (!silent) {
									cb.registerError(declaration+" is expected to be pure but accesses a non-immutable value which isn't private/protected "+fieldHandle, null);
								}
								pureImplementation = false;
							}
						}
					} else if (mh instanceof ThisHandle) {
						if (silent) {
							cb.registerError(declaration+" is expected to be pure but accesses 'this' "+mh, null);
						}
						pureImplementation = false;
					}
				}
			}
			return pureImplementation;
		}

		private boolean onCurrentObject(FieldHandle fh, Field f) {
			if (fh.getDeclaringClass().equals(declaration.getDeclaringClass())) {
				// ok, it's the same class, but is it the same instance?  This is 
				// a cheap way to work it out, but not the best.
				return (Modifier.isPrivate(f.getModifiers()) || Modifier.isProtected(f.getModifiers())) &&
					 (!Modifier.isStatic(f.getModifiers()));
			}
			
			return false;
		}

		public boolean checkInterfacePurity(Callback cb) {
			if (pureInterface == null) {
				pureInterface = true;
				if (PURE_LIST.contains(declaration.toString())) {
					return true;
				}
				
				if (IGNORE_EQUALS_PARAMETER_PURITY) {
					if (("equals".equals(declaration.getName())) && 
							("(Ljava/lang/Object;)Z".equals(declaration.getDesc()))) {
						return true;
					}
				}
	
				// check the signature of the pure method
				if (declaration instanceof MethodHandle) {
					Method m = ((MethodHandle) declaration).hydrate(cl);
					for (Type t : m.getGenericParameterTypes()) {
						if (!typeIsImmutable(t, cb)) {
							if (!silent) {
								cb.registerError("Pure method "+declaration+" can't take non-immutable argument "+t, null);
							}
							pureInterface = false;
						}
					}
				}
				
				if (declaration instanceof ConstructorHandle) {
					Constructor<?> m = ((ConstructorHandle) declaration).hydrate(cl);
					for (Type t : m.getGenericParameterTypes()) {
						if (!typeIsImmutable(t, cb)) {
							if (!silent) {
								cb.registerError("Pure method "+declaration+" can't take non-immutable argument "+t, null);
							}
							pureInterface = false;
						}
					}
				}
			} 
			return pureInterface;
			
		}

		public boolean checkPureMethodOutsideProject(Callback cb) {
			if (pureImplementation == null) {
				if (declaration instanceof MethodHandle) {
					pureImplementation = isPureCall((MethodHandle) declaration, cb);
				} else if (declaration instanceof ConstructorHandle) {
					pureImplementation = isPureCall((ConstructorHandle) declaration, cb); 
				} 
				
				if (!pureImplementation) {
					if (!silent) {
						cb.registerError(this+" required to be pure, but isn't.  Consider overriding this method.", null);
					}
				}
			}
			
			return pureImplementation;
		}



		private PurityChecker getOuterType() {
			return PurityChecker.this;
		}
	}
	
	static {
		@SuppressWarnings({ "rawtypes" })
		List l = Arrays.asList(Byte.class, Float.class, Double.class, Integer.class, String.class, Character.class, 
			Long.class, Boolean.class, Short.class, BigDecimal.class, BigInteger.class);
		INBUILT_IMMUTABLE_CLASSES.addAll(l);
	}
	
	private ClassLoader cl;

	public PurityChecker(ClassLoader cl) {
		this.cl = cl;
	}
	
	@Override
	public void checkModel(ProjectModel pm, Callback cb) {
		Set<PureMethod> pureList = createPureMethodList(pm);
		addMethodsFromImmutableValueClassToPureList(pm, cb, pureList);
		addMethodsFromPureClassToPureList(pm, cb, pureList);
		lookForNotPureMethods(pureList, pm, cb);
		outputPureMethodList(pureList, cb, pm);
	}

	private void outputPureMethodList(Set<PureMethod> pureList, Callback cb, ProjectModel pm) {
		for (PureMethod pureMethod : pureList) {
			if ((pureMethod.pureImplementation) && (pureMethod.pureInterface)) {
				if (pm.getAllClasses().contains(pureMethod.declaration.getDeclaringClass())) {
					cb.registerPure(pureMethod.declaration.toString());
				}
			}
		}
	}

	private void addMethodsFromImmutableValueClassToPureList(ProjectModel pm, Callback cb, Set<PureMethod> pureList) {
		for (String className : pm.getAllClasses()) {
			Class<?> cl = hydrate(className);
			if (classIsImmutable(cl, cb)) {
				Class<?> immutableClass = hydrate(className);
				if (isConcrete(immutableClass)) {
					doClassChecks(immutableClass, cb);
					addMethodsFromClassToPureList(immutableClass, pureList, cb, true); 
				}
			}
		}
	}

	protected boolean isConcrete(Class<?> someClass) {
		return !Modifier.isAbstract(someClass.getModifiers()) && !Modifier.isInterface(someClass.getModifiers());
	}
	
	private void addMethodsFromPureClassToPureList(ProjectModel pm, Callback cb, Set<PureMethod> pureList) {
		for (String className : pm.getAllClasses()) {
			Class<?> cl = hydrate(className);
			if (classIsPure(cl, cb)) {
				Class<?> pureClass = hydrate(className);
				if (isConcrete(pureClass)) {
					addMethodsFromClassToPureList(pureClass, pureList, cb, false);
				}
			}
		}
	}

	private void lookForNotPureMethods(Set<PureMethod> pureList, ProjectModel pm, Callback cb) {
		Set<String> inScope = new HashSet<String>(pm.getAllClasses());
		Map<MemberHandle, PureMethod> pureMethodLookup = createPureLookup(pureList);
		for (PureMethod pureCandidate : pureList) {
			cb.send("Checking pure method: "+pureCandidate);
			pureCandidate.checkInterfacePurity(cb); 
			if (inScope.contains(pureCandidate.declaration.getDeclaringClass())) {
				boolean isImplementationPure = pureCandidate.checkImplementationPurity(cb, pm);
				
				if (!isImplementationPure) {
					cascadeImpurity(pureMethodLookup, pureCandidate, pm, cb);
				}
				
			} else {
				pureCandidate.checkPureMethodOutsideProject(cb);
			}
		}
	}
	
	private void cascadeImpurity(Map<MemberHandle, PureMethod> pureMethodLookup, PureMethod pureCandidate, ProjectModel pm, Callback cb) {
		for (MemberHandle user : pm.getCalls(pureCandidate.declaration)) {
			PureMethod toDemote = pureMethodLookup.get(user);
			if (toDemote == null) {
				cb.send("Couldn't find "+user+" in code base to demote");
			} else {
				if ((toDemote.e == Enforcement.CHECKED) && (toDemote.checkImplementationPurity(cb, pm))) {
					cb.send(toDemote+" is not pure because it uses "+pureCandidate+", which is not pure");
					toDemote.pureImplementation = false;
					cascadeImpurity(pureMethodLookup, toDemote, pm, cb);
				}
			}
		}
	}

	private Map<MemberHandle, PureMethod> createPureLookup(Set<PureMethod> pmList) {
		Map<MemberHandle, PureMethod> out = new HashMap<MemberHandle, PurityChecker.PureMethod>(pmList.size() * 3);
		for (PureMethod pureMethod : pmList) {
			out.put(pureMethod.declaration, pureMethod);
		}
		return out;
	}


	private boolean isPureCall(ConstructorHandle mh, Callback cb) {
		if (PURE_LIST.contains(mh.toString())) {
			return true;
		}
		
		Constructor<?> m = mh.hydrate(cl);
		Pure p = m.getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		p = m.getDeclaringClass().getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		if (classIsImmutable(m.getDeclaringClass(), cb)) {
			return true;
		}
		
		if (IGNORE_EXCEPTION_CONSTRUCTION) {
			if (Throwable.class.isAssignableFrom(m.getDeclaringClass())) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isPureCall(MethodHandle mh, Callback cb) {
		if (PURE_LIST.contains(mh.toString())) {
			return true;
		}
		
		if (IGNORE_EQUALS_PARAMETER_PURITY) {
			if (("equals".equals(mh.getName())) && ("(Ljava/lang/Object;)Z".equals(mh.getDesc()))) {
				return true;
			}
		}
		
		Method m = mh.hydrate(cl);
		Pure p = m.getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		p = m.getDeclaringClass().getAnnotation(Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}
		
		if (classIsImmutable(m.getDeclaringClass(), cb)) {
			return true;
		}
		
		return false;
	}

	private void doClassChecks(Class<?> immutableClass, Callback cb) {
		if (!Modifier.isFinal(immutableClass.getModifiers())) {
			cb.registerError("Concrete @ImmutableValue class should be final: "+immutableClass.getName(), null);
		}
		
		while (immutableClass != Object.class) {
			for (Field f : immutableClass.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					if (!Modifier.isFinal(f.getModifiers())) {
						cb.registerError("Field "+f.getName()+" should be final on @ImmutableValue class "+immutableClass.getName(), null);
					}
				}
				
				if (!typeIsImmutable(f.getGenericType(), cb)) {
					cb.registerError("Field "+f.getName()+" should have an immutable type on class "+
							immutableClass+".  Consider adding @ImmutableValue to "+f.getType(), null);
				}
			}
			
			immutableClass = immutableClass.getSuperclass();
		}
	}

	private boolean typeIsImmutable(Type t, Callback cb) {
		// check if it's in the immutable list
		Class<?> c;
		if (t instanceof Class) {
			c = (Class<?>) t;
		} else {
			// todo
			c = null;
		}
		
		
		return classIsImmutable(c, cb);
	}

	private void addMethodsFromClassToPureList(Class<?> pureClass, Set<PureMethod> pureList, Callback cb, boolean entireExposedInterface) {
		List<PureMethod> addedSoFar = new LinkedList<PureMethod>();
		Class<?> in = pureClass;
		while (entireExposedInterface ? (in != null) : (in != Object.class)) {
			for (Method m : in.getDeclaredMethods()) {
				MethodHandle mh = new MethodHandle(m);
				boolean visible = entireExposedInterface ? methodIsVisible(m, pureClass) : true;
				Pure p = m.getAnnotation(Pure.class);
				Enforcement e = p == null ? Enforcement.CHECKED : p.value();
				addedSoFar.add(new PureMethod(pureClass, mh, e, !visible));
			}
			
			in = in.getSuperclass();
		}
		
		pureList.addAll(addedSoFar);
	}

	protected boolean methodIsVisible(Method m, Class<?> pureClass) {
		if (Modifier.isPrivate(m.getModifiers())) {
			return false;
		}
		
		if (Modifier.isProtected(m.getModifiers())) {
			return false;
		}
 		
		
		Class<?> onClass = m.getDeclaringClass();
		while (pureClass != onClass) {
			Method over;
			try {
				over = pureClass.getDeclaredMethod(m.getName(), m.getParameterTypes());
				if (over != null) {
					return false;
				}
			} catch (Exception e) {
			}
			
			pureClass = pureClass.getSuperclass();
		}
		
		return true;
	}

	protected Class<?> hydrate(String className) {
		return new ClassHandle(className).hydrate(cl);
	}

	private Set<PureMethod> createPureMethodList(ProjectModel pm) {
		Set<PureMethod> out = new LinkedHashSet<PureMethod>();
		for (MemberHandle handle : pm.getMembersWithAnnotation(getInternalName(Pure.class))) {
			if (handle instanceof MethodHandle) {
				Method m = ((MethodHandle)handle).hydrate(cl);
				Pure p = m.getAnnotation(Pure.class);
				if (p.value() != Enforcement.NOT_PURE) {
					out.add(new PureMethod(m.getDeclaringClass(), (MethodHandle) handle, p.value(), false));
				}
			}
		}
		
		return out;
	}

	protected String getInternalName(Class<?> in) {
		return org.pure4j.model.Type.getInternalName(in);
	}
	

	private Map<String, Boolean> immutableClasses = new HashMap<String, Boolean>();

	private boolean classIsPure(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}
		
		Pure p = in.getAnnotation(Pure.class);
		if ((p != null) && (p.value() != Enforcement.NOT_PURE)) {
			return true;
		}
		
		return false;
	}
	
	private boolean classIsImmutable(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}

		if (in.isPrimitive()) {
			return true;
		}
		
		if (INBUILT_IMMUTABLE_CLASSES.contains(in)) {
			return true;
		}
		
		
		if (!immutableClasses.containsKey(in.getCanonicalName())) {
			cb.send("Checking Class For @ImmutableValue: " + in.getCanonicalName());
			boolean immutable = false;
			ImmutableValue ann = in.getAnnotation(ImmutableValue.class);
			immutable = immutable || (ann != null);
			immutable = immutable || ((in.getSuperclass() != null) ? classIsImmutable(in.getSuperclass(), cb) : false);
			for (Class<?> interf : in.getInterfaces()) {
				immutable = immutable || classIsImmutable(interf, cb);
			}
			
			if (immutable) {
				cb.send("Found immutable class: "+in.getCanonicalName());
				immutableClasses.put(in.getCanonicalName(), immutable);
			}
			return immutable;
		} else {
			return immutableClasses.get(in.getCanonicalName());
		}
	}
}
