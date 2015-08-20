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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pure4j.annotations.immutable.ImmutableValue;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.FieldHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.ProjectModel;

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

		public PureMethod(Class<?> usedFrom, MemberHandle declaration, Enforcement e) {
			super();
			this.usedFrom = usedFrom;
			this.declaration = declaration;
			this.e = e;
		}

		@Override
		public String toString() {
			return "PureMethod [usedFrom=" + usedFrom + ", declaration="
					+ declaration + ", e=" + e + "]";
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
							cb.registerError("Pure method "+declaration+" can't take non-immutable argument "+t, null);
							pureInterface = false;
						}
					}
				}
				
				if (declaration instanceof ConstructorHandle) {
					Constructor<?> m = ((ConstructorHandle) declaration).hydrate(cl);
					for (Type t : m.getGenericParameterTypes()) {
						if (!typeIsImmutable(t, cb)) {
							cb.registerError("Pure method "+declaration+" can't take non-immutable argument "+t, null);
							pureInterface = false;
						}
					}
				}
			} 
			return pureInterface;
			
		}

		public boolean checkPureMethodOutsideProject(Callback cb) {
			if (declaration instanceof MethodHandle) {
				return isPureCall((MethodHandle) declaration, cb);
			} else if (declaration instanceof ConstructorHandle) {
				return isPureCall((ConstructorHandle) declaration, cb); 
			} else {
				return true;
			}
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
		List<PureMethod> pureList = createPureList(pm);
		for (String className : pm.getAllClasses()) {
			Class<?> cl = hydrate(className);
			if (classIsImmutable(cl, cb)) {
				Class<?> immutableClass = hydrate(className);
				if (!Modifier.isAbstract(immutableClass.getModifiers())) {
					doClassChecks(immutableClass, cb);
					addMethodsFromImmutablesToPureList(immutableClass, pureList, cb);
				}
			}
		}
		
		lookForNotPureMethods(pureList, pm, cb, new HashSet<String>(pm.getAllClasses()));
	}

	private void lookForNotPureMethods(List<PureMethod> pureList, ProjectModel pm, Callback cb, Set<String> inScope) {
		for (PureMethod meth : pureList) {
			cb.send("Checking pure method: "+meth);
			meth.checkInterfacePurity(cb); 
		
			if (inScope.contains(meth.declaration.getDeclaringClass())) {
				// check what the pure method does
				MemberHandle methodHandle = meth.declaration;
				for (MemberHandle mh: pm.getCalls(methodHandle)) {
					if (mh instanceof MethodHandle) {
						if (!isPureCall((MethodHandle) mh, cb)) {
							cb.registerError(methodHandle+" is expected to be a pure method, but calls impure method "+mh, null);
						}
					} else if (mh instanceof ConstructorHandle) {
						if (!isPureCall((ConstructorHandle) mh, cb)) {
							cb.registerError(methodHandle+" is expected to be a pure method, but calls impure constructor "+mh, null);
						}
					} else if (mh instanceof FieldHandle) {
						FieldHandle fieldHandle = (FieldHandle)mh;
						Field f = fieldHandle.hydrate(cl);
						
						if ((!Modifier.isFinal(f.getModifiers()))) {
							cb.registerError(methodHandle+" is expected to be pure but accesses non-final field "+fieldHandle, null);
						}
						
						if (!typeIsImmutable(f.getGenericType(), cb)) {
							cb.registerError(methodHandle+" is expected to be pure but accesses a non-immutable value "+fieldHandle, null);
						}
					}
				}
			} else {
				if (!meth.checkPureMethodOutsideProject(cb)) {
					cb.registerError(meth+" required to be pure, but isn't.  Consider overriding this method.", null);
				}
			}
		}
	}


	private boolean isPureCall(ConstructorHandle mh, Callback cb) {
		if (PURE_LIST.contains(mh.toString())) {
			return true;
		}
		
		Constructor<?> m = mh.hydrate(cl);
		Pure p = m.getAnnotation(Pure.class);
		if (p != null) {
			return true;
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
			return true;
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

	private void addMethodsFromImmutablesToPureList(Class<?> immutableClass, List<PureMethod> pureList, Callback cb) {
		List<PureMethod> addedSoFar = new LinkedList<PureMethod>();
		Class<?> in = immutableClass;
		while (in != null) {
			for (Method m : in.getDeclaredMethods()) {
				MethodHandle mh = new MethodHandle(m);
				if (!methodOverridden(addedSoFar, mh)) {
					Pure p = m.getAnnotation(Pure.class);
					addedSoFar.add(new PureMethod(immutableClass, mh, p == null ? Enforcement.FULL : p.value()));
				}
			}
			
			in = in.getSuperclass();
		}
		
		pureList.addAll(addedSoFar);
	}

	protected boolean methodOverridden(List<PureMethod> addedSoFar, MethodHandle mh) {
		for (PureMethod m : addedSoFar) {
			if (((MethodHandle)m.declaration).overrides(mh)) {
				return true;
			}
		}
		
		return false;
	}

	protected Class<?> hydrate(String className) {
		return new ClassHandle(className).hydrate(cl);
	}

	private List<PureMethod> createPureList(ProjectModel pm) {
		List<PureMethod> out = new LinkedList<PureMethod>();
		for (MemberHandle handle : pm.getMembersWithAnnotation(getInternalName(Pure.class))) {
			if (handle instanceof MethodHandle) {
				Method m = ((MethodHandle)handle).hydrate(cl);
				Pure p = m.getAnnotation(Pure.class);
				out.add(new PureMethod(m.getDeclaringClass(), (MethodHandle) handle, p.value()));
			}
		}
		
		return out;
	}

	protected String getInternalName(Class<?> in) {
		return org.pure4j.model.Type.getInternalName(in);
	}
	

	private Map<String, Boolean> immutableClasses = new HashMap<String, Boolean>();

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
