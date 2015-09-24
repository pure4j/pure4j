package org.pure4j.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pure4j.annotations.immutable.IgnoreNonImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Mutability;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.exception.ClassExpectingPureMethod;
import org.pure4j.exception.IncorrectPure4JImmutableCallException;
import org.pure4j.exception.MemberCantBeHydratedException;
import org.pure4j.exception.MissingImmutableParameterCheckException;
import org.pure4j.exception.PureMethodAccessesNonImmutableFieldException;
import org.pure4j.exception.PureMethodAccessesSharedFieldException;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.exception.PureMethodNotInProjectScopeException;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.exception.PureMethodReturnNotImmutableException;
import org.pure4j.model.CallHandle;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.FieldHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.StackArgumentsCall;
import org.springframework.asm.Opcodes;

/**
 * The responsibility of this class is to keep track of all the methods we want
 * to check for purity. The {@link PureMethod} implementations track each
 * individual call and self-check the purity. This is done on a per
 * method-declaration level.
 * 
 * @author robmoffat
 *
 */
public class PureChecklistHandler {

	public static final boolean IGNORE_EXCEPTION_CONSTRUCTION = true;
	public static final boolean IGNORE_EQUALS_PARAMETER_PURITY = true;
	public static final boolean IGNORE_TOSTRING_PURITY = true;
	public static final boolean IGNORE_ENUM_VALUES_PURITY = true;

	public class PureMethod {

		public MemberHandle declaration;
		public Enforcement e;
		public Mutability m;
		private Boolean pureImplementation = null;
		//private Boolean pureInterface = null;
		private Set<Class<?>> usedIn = new LinkedHashSet<Class<?>>();

		private PureMethod(MemberHandle declaration, Enforcement e, Mutability m) {
			super();
			this.declaration = declaration;
			this.e = e;
			this.m = m;
			setupEnforcements(e);
		}

		protected void setupEnforcements(Enforcement e) {
			if (e == Enforcement.FORCE) {
				pureImplementation = true;
			} else if (e == Enforcement.NOT_PURE) {
				pureImplementation = false;
			}
		}

		private PureMethod(String description, Enforcement e) throws ClassNotFoundException {
			int firstDot = description.indexOf(".");
			int firstBracket = description.indexOf("(");
			String classPart = description.substring(0, firstDot);
			String methodPart = description.substring(firstDot + 1, firstBracket);
			String descPart = description.substring(firstBracket);
			this.declaration = (methodPart.equals("<init>")) ? new ConstructorHandle(classPart, descPart, 0) : new MethodHandle(classPart, methodPart, descPart, 0);

			this.e = e;
			setupEnforcements(e);
		}

		@Override
		public String toString() {
			return "   See:[declaration=" + declaration + "\n        e=" + e + "\n        m=" + m + ", \n        usedIn=\n" + lines(usedIn, 10) + "       ]";
		}

		private String lines(Set<Class<?>> usedIn2, int i) {
			StringBuilder sb = new StringBuilder();
			for (Class<?> class1 : usedIn2) {
				for (int j = 0; j < i; j++) {
					sb.append(' ');
				}
				sb.append(class1.toString());
				sb.append("\n");
			}
			
			return sb.toString();
		}
//		
//		public boolean checkInterfacePurity(Callback cb, ProjectModel pm) {
//			id (pureInterface == null) {
//				pureInterface = true;
//				
//				
//				
//			}
//			
//		}

		public boolean checkImplementationPurity(Callback cb, ProjectModel pm) {
			if (pureImplementation == null) {
				try {
					pureImplementation = true;

					if (!pm.withinModel(declaration.getClassName())) {
						// we can't confirm that the method is pure, unless it
						// has been forced already.
						if (!isMarkedPure(declaration, cb)) {
							cb.registerError(new PureMethodNotInProjectScopeException(this));
						}
						pureImplementation = false;
					}
					
					if (declaration instanceof CallHandle) {
						if ((pm.getOpcodes((CallHandle) declaration) & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC) {
							pureImplementation = true;
							return true;
						}
					}

					if (this.declaration.getDeclaringClass(cl).isInterface()) {
						pureImplementation = true;
						return true;
					}

					if (IGNORE_ENUM_VALUES_PURITY && declaration.getDeclaringClass(cl).isEnum() && declaration.getName().equals("values")) {
						return true;
					}

					if (IGNORE_EQUALS_PARAMETER_PURITY) {
						if (("equals".equals(declaration.getName())) && ("(Ljava/lang/Object;)Z".equals(declaration.getDesc()))) {
							return true;
						}
					}

					if (declaration instanceof MethodHandle) {
						Method m = ((MethodHandle) declaration).hydrate(cl);
						if (Modifier.isAbstract(m.getModifiers())) {
							return true;
						}
					}

					
					// check the signature of accessible pure method
					
					if (isAccessibleOutsideClass(declaration, cl)) {
						Type[] genericTypes = declaration.getGenericTypes(cl);
						int argOffset = getArgOffset();
						for (int i = thisFieldSkip(); i < genericTypes.length; i++) {
							Type t = genericTypes[i];
							if (typeFailsCheck(cb, t)) {
								int argNo = i+ argOffset;
								if (!isRuntimeChecked(argNo, pm, cb)) {
									cb.registerError(new PureMethodParameterNotImmutableException(this, t));
									pureImplementation = false;
									return false;
								}
							}
						}
						
						// RETURN TYPE CHECKING 
						if (declaration instanceof MethodHandle) {
							if (declaration.getAnnotation(cl, IgnoreNonImmutableTypeCheck.class) == null) {
								Method m = ((MethodHandle) declaration).hydrate(cl);
								Type t = m.getGenericReturnType();
								if (typeFailsCheck(cb, t) && (!returnsOwnType()))  {
									cb.registerError(new PureMethodReturnNotImmutableException(this, t));
									pureImplementation = false;
									return false; 
								}
							}
						} 
					}

					
					List<MemberHandle> calls = pm.getCalls(declaration);
					for (MemberHandle mh : calls) {
						boolean staticCall = false;
						if (mh instanceof MethodHandle) {
							Method m = ((MethodHandle) mh).hydrate(cl);
							staticCall = Modifier.isStatic(m.getModifiers());
						}
						if (mh instanceof CallHandle) {
							if ((IGNORE_TOSTRING_PURITY) && (mh.getName().equals("toString")) && (mh.getDeclaringClass().equals("java/lang/Object"))) {
								// we can skip this one
							} else if (!isMarkedPure(mh, cb)) {
								cb.registerError(new PureMethodCallsImpureException(this, (CallHandle) mh));
								pureImplementation = false;
							}
						} else if (mh instanceof FieldHandle) {
							FieldHandle fieldHandle = (FieldHandle) mh;
							Field f = fieldHandle.hydrate(cl);
							boolean isStatic = Modifier.isStatic(f.getModifiers());
							boolean isFinal = Modifier.isFinal(f.getModifiers());
							
							if (isStatic) {
								if (!isFinal) {
									cb.registerError(new PureMethodAccessesSharedFieldException(this, fieldHandle));
									pureImplementation = false;
								}
								
								if (!immutables.typeIsMarked(f.getGenericType(), cb)) {
									if ((!forcedImmutable(f)) && (isAccessibleOutsideClass(fieldHandle, cl))) {
										cb.registerError(new PureMethodAccessesNonImmutableFieldException(this, fieldHandle));
										pureImplementation = false;
									}
								}
							} 
						}
					}
				} catch (MemberCantBeHydratedException e) {
					cb.registerError(e);
					pureImplementation = false;
				}

				cb.send("Pure implementation: " + this);

			}
			return pureImplementation;
		}

		private boolean returnsOwnType() {
			String returnedType = declaration.getDesc().substring(declaration.getDesc().lastIndexOf(")"));
			return (returnedType.contains(declaration.getDeclaringClass()));
		}

		protected boolean typeFailsCheck(Callback cb, Type t) {
			if (m == Mutability.ALLOW_IMMUTABLE_ONLY) {
				return !immutables.typeIsMarked(t, cb);
			} 
			
			return false;
		}

		private int thisFieldSkip() {
			if (declaration instanceof ConstructorHandle) {
				Constructor<?> c = ((ConstructorHandle)declaration).hydrate(cl);
				int countOfThisFields = 0;	// for 'this'
				for (Field f : c.getDeclaringClass().getDeclaredFields()) {
					if (f.getName().startsWith("this$")) {
						System.out.println("this field:"+f.getName());
						countOfThisFields++;
					}
				}
				
				return countOfThisFields > 0 ? 1 : 0;
			}
			
			return 0;
		}
		
		private int getArgOffset() {
			if (declaration instanceof ConstructorHandle) {
				return 1;
			} else {
				// method handle
				Method m = ((MethodHandle) declaration).hydrate(cl);
				if (Modifier.isStatic(m.getModifiers())) {
					return 0;
				} else {
					return 1;
				}
				
			}
		}

		private boolean forcedImmutable(Field f) {
			IgnoreNonImmutableTypeCheck iv = f.getAnnotation(IgnoreNonImmutableTypeCheck.class);
			if ((iv != null)) {
				return true;
			}

			return false;
		}

		/**
		 * Checks to see whether the developer has called the runtime check for
		 * immutability.
		 */
		private boolean isRuntimeChecked(int paramNo, ProjectModel pm, Callback cb) {
			List<MemberHandle> calls = pm.getCalls(declaration);
			if (isCovariantMethod(calls)) {
				return true;
			}
			boolean found = false;
			boolean checkTried = false;
			for (MemberHandle memberHandle : calls) {
				if (memberHandle instanceof StackArgumentsCall) {
					StackArgumentsCall icmh = (StackArgumentsCall) memberHandle;
					if (icmh.getName().equals("immutable")) {
						checkTried = true;
						if (!icmh.isFirstCall()) {
							cb.registerError(new IncorrectPure4JImmutableCallException(this));
							return false;
						} else if (icmh.getLocalVariables().contains(paramNo)) {
							found = true;
						}
					} else if (icmh.getName().equals("unsupported")) {
						checkTried = true;
						found = true;
					} else if (icmh.getName().equals("<init>")) {
						if (icmh.getLocalVariables().contains(paramNo)) {
							found = true;
						}
					}
				}
			}

			if (checkTried) {
				if (!found) {
					cb.registerError(new MissingImmutableParameterCheckException(this, paramNo));
					return false;
				}
			}

			return checkTried;
		}
		
		/**
		 * If this is a covariant return type implementation, we don't need to
		 * bother with checking, it'll be done in the more specific method.
		 */
		private boolean isCovariantMethod(List<MemberHandle> calls) {
			if (!(this.declaration instanceof MethodHandle)) {
				return false;
			}
			
			if ((calls.size() == 1) && (calls.get(0) instanceof MethodHandle)) {
				MethodHandle theCall = (MethodHandle) calls.get(0);
				if (theCall.getName().equals(this.declaration.getName())) {
					Method theCallMethod = theCall.hydrate(cl);
					Method fromMethod = ((MethodHandle) this.declaration).hydrate(cl);
					if (theCallMethod.getReturnType().isAssignableFrom(fromMethod.getReturnType())) {
						if (theCallMethod.getParameters().length == fromMethod.getParameters().length) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	private ClassLoader cl;
	private ClassAnnotationCache immutables;
	private ClassAnnotationCache mutableUnshared;

	public PureChecklistHandler(ClassLoader cl, ClassAnnotationCache immutables, ClassAnnotationCache mutableUnshared) {
		super();
		this.cl = cl;
		this.immutables = immutables;
		this.mutableUnshared = mutableUnshared;
		loadPureLists();
	}

	private Map<MemberHandle, PureMethod> pureChecklist = new HashMap<MemberHandle, PureMethod>();

	public void loadPureLists() {
		try {
			load("/java-lang.pure");
			load("/java-builtins.pure");
			load("/java-extra.pure");
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load the pure lists: ", e);
		}
	}

	private void load(String fileName) throws IOException, ClassNotFoundException {
		InputStream is = PureChecklistHandler.class.getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = br.readLine();
		while (line != null) {
			String[] parts = line.trim().split(" ");
			Enforcement e = Enforcement.valueOf(parts[0]);
			PureMethod pureMethod = new PureMethod(parts[1], e);
			pureChecklist.put(pureMethod.declaration, pureMethod);
			line = br.readLine();
		}
	}

	private static boolean isAnonymousInnerClass(String className) {
		String tail = className.substring(className.lastIndexOf("$")+1);
		return tail.matches("[0-9]*");
	}
	
	
	public boolean isMarkedPure(MemberHandle mh, Callback cb) {
		boolean staticMethod = false;
		if (mh instanceof MethodHandle) {
			staticMethod = Modifier.isStatic(mh.getModifiers(cl));
		}
		
		if (pureChecklist.containsKey(mh)) {
			PureMethod pm = pureChecklist.get(mh);
			return pm.e != Enforcement.NOT_PURE;
		}

		if (IGNORE_EQUALS_PARAMETER_PURITY) {
			if (("equals".equals(mh.getName())) && ("(Ljava/lang/Object;)Z".equals(mh.getDesc()))) {
				return true;
			}
		}
		
		if ((mh instanceof ConstructorHandle) && (isAnonymousInnerClass(mh.getClassName()))) {
			// because you can't add the pure annotation to anonymous inner classes, we assume it is pure.
			return true;
		}
			
		

		Pure p = mh.getAnnotation(cl, Pure.class);
		if (p != null) {
			return p.value() != Enforcement.NOT_PURE;
		}

		if (staticMethod) {
			if (isInnerClassAccessMethod(mh, cb)) {
				return true;
			}
			
			return false;
		}

		// interfaces now allowed as pure
		if (mh.getDeclaringClass(cl).isInterface()) {
			return true;
		}

		if (isMarkedPure(mh.getDeclaringClass(cl), cb)) {
			return true;
		}

		if (IGNORE_EXCEPTION_CONSTRUCTION) {
			if (Throwable.class.isAssignableFrom(mh.getDeclaringClass(cl))) {
				return true;
			}
		}

		return false;
	}

	private boolean isInnerClassAccessMethod(MemberHandle mh, Callback cb) {
		if (mh instanceof MethodHandle) {
			Method m = ((MethodHandle) mh).hydrate(cl);
			if ((m.getName().startsWith("access$")) && 
					(m.getParameterTypes().length >= 1)) {
				
				Type t = m.getGenericParameterTypes()[0];
				if (t instanceof Class<?>) {
					return isMarkedPure((Class<?>) t, cb);
				}
			}
		}	
		return false;
	}
	
	public boolean isMarkedPure(Class<?> in, Callback cb) {
		if ((in == null) || (in == Object.class)) {
			return false;
		}

		if (immutables.classIsMarked(in, cb)) {
			return true;
		}
		
		if (mutableUnshared.classIsMarked(in, cb)) {
			return true;
		}
		
		Pure p = in.getAnnotation(Pure.class);
		if ((p != null) && (p.value() != Enforcement.NOT_PURE)) {
			return true;
		}

		for (Class<?> intf : in.getInterfaces()) {
			if (isMarkedPure(intf, cb)) {
				return true;
			}
		}

		if (isMarkedPure(in.getSuperclass(), cb)) {
			return true;
		}

		return false;
	}

	public void addMethod(CallHandle declaration, Enforcement e, Mutability m, Class<?> usedIn, Callback cb) {
		PureMethod pm;
		if (pureChecklist.containsKey(declaration)) {
			pm = pureChecklist.get(declaration);
		} else {
			pm = new PureMethod(declaration, e, m);
			pureChecklist.put(declaration, pm);
			cb.send("  - " + declaration + " " + e);
		}

		if (pm.e != e) {
			if (pm.e != Enforcement.FORCE) {
				cb.registerError(new ClassExpectingPureMethod(usedIn, pm));
			}
		}

		pm.usedIn.add(usedIn);
	}

	public PureMethod getElementFor(MemberHandle mh) {
		return pureChecklist.get(mh);
	}

	public static boolean methodIsVisible(Method m, Class<?> pureClass) {
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

	public void doPureMethodChecks(Callback cb, ProjectModel pm) {
		for (PureMethod pureCandidate : pureChecklist.values()) {
			//pureCandidate.c
			pureCandidate.checkImplementationPurity(cb, pm);
		}
	}

	public Collection<PureMethod> getMethodList() {
		return pureChecklist.values();
	}

	public static boolean isAccessibleOutsideClass(MemberHandle handle, ClassLoader cl) {
		boolean pub = Modifier.isPublic(handle.getModifiers(cl));
		boolean priv = Modifier.isPrivate(handle.getModifiers(cl));
		boolean prot = Modifier.isProtected(handle.getModifiers(cl));
		return pub || ((!priv) && (!prot));
	}
}
