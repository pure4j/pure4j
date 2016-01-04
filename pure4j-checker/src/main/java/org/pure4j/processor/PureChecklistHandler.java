package org.pure4j.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pure4j.Pure4J;
import org.pure4j.annotations.immutable.IgnoreImmutableTypeCheck;
import org.pure4j.annotations.pure.Enforcement;
import org.pure4j.annotations.pure.Pure;
import org.pure4j.annotations.pure.PurityType;
import org.pure4j.exception.ClassExpectingPureMethod;
import org.pure4j.exception.ClassHierarchyUsesBothTypesOfPurity;
import org.pure4j.exception.IncorrectPure4JImmutableCallException;
import org.pure4j.exception.MemberCantBeHydratedException;
import org.pure4j.exception.MissingImmutableParameterCheckException;
import org.pure4j.exception.PureMethodAccessesNonFinalFieldException;
import org.pure4j.exception.PureMethodAccessesNonImmutableFieldException;
import org.pure4j.exception.PureMethodCallsImpureException;
import org.pure4j.exception.PureMethodNotInProjectScopeException;
import org.pure4j.exception.PureMethodOnNonImmutableClassException;
import org.pure4j.exception.PureMethodParameterNotImmutableException;
import org.pure4j.exception.PureMethodReturnNotImmutableException;
import org.pure4j.model.ArgumentedDeclarationHandle;
import org.pure4j.model.CallHandle;
import org.pure4j.model.CallInfo;
import org.pure4j.model.DeclarationHandle;
import org.pure4j.model.GenericTypeHandle;
import org.pure4j.model.ProjectModel;
import org.pure4j.model.StackArgumentsCallHandle;
import org.pure4j.model.impl.AbstractMemberHandle;
import org.pure4j.model.impl.AnnotationHandle;
import org.pure4j.model.impl.ClassHandle;
import org.pure4j.model.impl.ClassInitHandle;
import org.pure4j.model.impl.ConstructorDeclarationHandle;
import org.pure4j.model.impl.FieldDeclarationHandle;
import org.pure4j.model.impl.MethodDeclarationHandle;
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

		public DeclarationHandle declaration;
		public Enforcement implPurity;
		public Enforcement intfPurity;
		private Boolean pureImplementation = null;
		private Boolean pureInterface = null;
		private PurityType pt;
		private Set<ClassHandle> usedIn = new LinkedHashSet<ClassHandle>();

		public Set<ClassHandle> getUsedIn() {
			return usedIn;
		}

		private PureMethod(DeclarationHandle declaration, Enforcement impl, Enforcement intf, PurityType pt) {
			super();
			this.declaration = declaration;
			this.implPurity = impl;
			this.intfPurity = intf;
			setupEnforcements(intf, impl);
			this.pt = pt;
		}

		protected void setupEnforcements(Enforcement intf, Enforcement impl) {
			if (impl == Enforcement.FORCE) {
				pureImplementation = true;
				pureInterface = true;
			} else if (impl == Enforcement.NOT_PURE) {
				pureImplementation = false;
				pureInterface = false;
			}
			
			if (intf == Enforcement.FORCE) {
				pureInterface = true;
			} else if (intf == Enforcement.NOT_PURE) {
				pureInterface = false;
			}
		}

		private PureMethod(String description, Enforcement impl, Enforcement intf) throws ClassNotFoundException {
			int firstDot = description.indexOf(".");
			int firstBracket = description.indexOf("(");
			String classPart = description.substring(0, firstDot);
			String methodPart = description.substring(firstDot + 1, firstBracket);
			String descPart = description.substring(firstBracket);
			this.declaration = (methodPart.equals("<init>")) ? 
				new ConstructorDeclarationHandle(classPart, descPart, 0, 0, null, null) : 
				new MethodDeclarationHandle(classPart, methodPart, descPart, 0, 0, null, null, null);

			this.implPurity = impl;
			this.intfPurity = intf;
			setupEnforcements(intf, impl);
		}

		@Override
		public String toString() {
			return "   See:[declaration=" + declaration + "\n     impl=" + implPurity + "\n     intf=" + intfPurity + ", \n     purity=" + pt+", \n     usedIn=\n" + lines(usedIn, 10) + "       ]";
		}

		private String lines(Set<ClassHandle> usedIn2, int i) {
			StringBuilder sb = new StringBuilder();
			for (ClassHandle class1 : usedIn2) {
				for (int j = 0; j < i; j++) {
					sb.append(' ');
				}
				sb.append(class1.toString());
				sb.append("\n");
			}
			
			return sb.toString();
		}
		
		public Boolean checkInterfacePurity(Callback cb, ProjectModel pm) {
			if ((pureInterface == null) && (checkInterface)) {
				pureInterface = true;
				
				if (!pm.withinModel(declaration.getClassName())) {
					return pureInterface;
				}
				
				if (IGNORE_EQUALS_PARAMETER_PURITY) {
					if (("equals".equals(declaration.getName())) && ("(Ljava/lang/Object;)Z".equals(declaration.getDesc()))) {
						return true;
					}
				}

				
				// check the signature of accessible pure method.
				// only public/package visible methods need to be checked.
				if (declaration instanceof ArgumentedDeclarationHandle) {
					ArgumentedDeclarationHandle adeclaration = (ArgumentedDeclarationHandle) declaration;
					if (isAccessibleOutsideClass(adeclaration, cl, pm)) {
						GenericTypeHandle[] genericTypes = adeclaration.getGenericTypes();
						int argOffset = getArgOffset();
						for (int i = thisFieldSkip(); i < genericTypes.length; i++) {
							GenericTypeHandle t = genericTypes[i];
							if (typeFailsImmutabilityCheck(cb, t, pm)) {
								int argNo = i+ argOffset;
								if (!isRuntimeChecked(argNo, pm, cb)) {
									cb.registerError(new PureMethodParameterNotImmutableException(this, t));
									pureInterface = false;
									return false;
								}
							}
						}
						
						if (pt == PurityType.IMMUTABLE_VALUE) {
							if (adeclaration.isStatic()) {
								CallInfo ci = pm.getOpcodes(adeclaration);
								if (ci.usesThis()) {
									// if we use 'this', then the class must be immutable.
									for (ClassHandle c : usedIn) {
										if (typeFailsImmutabilityCheck(cb, c, pm)) {
											cb.registerError(new PureMethodOnNonImmutableClassException(this, c));
											pureInterface = false;
											return false;
										}
									}
								}
							}
						}
						
						
						if (pt == PurityType.MUTABLE_UNSHARED) {
							if (adeclaration instanceof MethodDeclarationHandle) {
								if (adeclaration.getAnnotation(IgnoreImmutableTypeCheck.class) == null) {
									GenericTypeHandle t = ((MethodDeclarationHandle) adeclaration).getGenericType();
									if (typeFailsImmutabilityCheck(cb, t, pm) && (!returnsOwnType()))  {
										pureInterface = isRuntimeReturnChecked(pm, cb);
									}
								}
							} 
						}
					}
				}
			}
			
			return pureInterface;
		}

		public Boolean checkImplementationPurity(Callback cb, ProjectModel pm) {
			if ((pureImplementation == null) && (checkImpl)) {
				try {
					pureImplementation = true;

					if (!pm.withinModel(declaration.getClassName())) {
						// we can't confirm that the method is pure, unless it
						// has been forced already.
						if (isMarkedPure(declaration, cb, pm)) {
							pureImplementation = true;
							return true;
						} else {
							cb.registerError(new PureMethodNotInProjectScopeException(this));
							pureImplementation = false;
							return false;
						}
					}
					
					CallInfo ci = pm.getOpcodes(declaration);
					if ((ci.getOpcodes() & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC) {
						// synthetics are always pure.
						pureImplementation = true;
						return true;
					}

					if (this.declaration.getDeclaringClass().isInterface()) {
						pureImplementation = true;
						return true;
					}

					if (IGNORE_ENUM_VALUES_PURITY && declaration.getDeclaringClass().isEnum() && declaration.getName().equals("values")) {
						return true;
					}

					if (declaration instanceof MethodDeclarationHandle) {
						if (((MethodDeclarationHandle) declaration).isAbstract()) {
							return true;
						}
					}
					
					List<CallHandle> calls = pm.getCalls(declaration);
					for (CallHandle mh : calls) {
						if (mh instanceof DeclarationHandle) {
							mh = ensureCorrectClass(mh);
							if ((IGNORE_TOSTRING_PURITY) && (mh.getName().equals("toString")) && (mh.getClassName().equals("java/lang/Object"))) {
								// we can skip this one
							} else if (!isMarkedPure((DeclarationHandle) mh, cb, pm)) {
								cb.registerError(new PureMethodCallsImpureException(this, mh));
								pureImplementation = false;
							}
						} else if (mh instanceof FieldDeclarationHandle) {
							FieldDeclarationHandle fieldHandle = (FieldDeclarationHandle) mh;
							boolean isStatic = fieldHandle.isStatic();
							boolean isFinal = fieldHandle.isFinal();
							
							if (isStatic) {
								if (!isFinal) {
									cb.registerError(new PureMethodAccessesNonFinalFieldException(this, fieldHandle));
									pureImplementation = false;
								}
									
								if (!immutables.typeIsMarked(fieldHandle.getGenericType(), cb, pm)) {
									if ((!forcedImmutable(fieldHandle)) && (isAccessibleOutsideClass(fieldHandle, cl, pm))) {
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
			return (returnedType.contains(declaration.getDeclaringClass().getDescriptor()));
		}

		protected boolean typeFailsImmutabilityCheck(Callback cb, GenericTypeHandle t, ProjectModel pm) {
			if (intfPurity == Enforcement.CHECKED) {
				return !immutables.typeIsMarked(t, cb, pm);
			} 
			
			return false;
		}

		private int thisFieldSkip() {
			if (declaration instanceof ConstructorDeclarationHandle) {
				int countOfThisFields = 0;	// for 'this'
				for (FieldDeclarationHandle f : ((ConstructorDeclarationHandle)declaration).getDeclaringClass().getDeclaredFields()) {
					if (f.getName().startsWith("this$")) {
						countOfThisFields++;
					}
				}
				
				return countOfThisFields > 0 ? 1 : 0;
			}
			
			return 0;
		}
		
		private int getArgOffset() {
			if (declaration instanceof ConstructorDeclarationHandle) {
				return 1;
			} else if (declaration instanceof ClassInitHandle) {
				return 0;
			} else {
				// method handle
				if (((MethodDeclarationHandle) declaration).isStatic()) {
					return 0;
				} else {
					return 1;
				}
				
			}
		}

		private boolean forcedImmutable(FieldDeclarationHandle f) {
			AnnotationHandle iv = f.getAnnotation(IgnoreImmutableTypeCheck.class);
			if ((iv != null)) {
				return true;
			}

			return false;
		}

		private boolean isRuntimeReturnChecked(ProjectModel pm, Callback cb) {
			CallInfo ci = pm.getOpcodes(declaration);
			
			for (Object mh : ci.getMethodsBeforeReturns()) {
				if (mh instanceof Integer) {
					int lineNumber = (Integer) mh;
					cb.registerError(new PureMethodReturnNotImmutableException(this, lineNumber));
					return false;
				} else if (mh instanceof DeclarationHandle) {
					AbstractMemberHandle ch = (AbstractMemberHandle) mh;
					if (ch.getClassName().equals(org.springframework.asm.Type.getInternalName(Pure4J.class)) &&
						(ch.getName().equals("returnImmutable")) ) {
					// return ok.
					
					} else {
						cb.registerError(new PureMethodReturnNotImmutableException(this, ch.getLine()));
						return false;
					}
				}
			}
			
			return true;
		}
		
		/**
		 * Checks to see whether the developer has called the runtime check for
		 * immutability.
		 */
		private boolean isRuntimeChecked(int paramNo, ProjectModel pm, Callback cb) {
			List<CallHandle> calls = pm.getCalls(declaration);
			if (isCovariantMethod(calls)) {
				return true;
			}
			boolean found = false;
			boolean checkTried = false;
			for (CallHandle memberHandle : calls) {
				if (memberHandle instanceof StackArgumentsCallHandle) {
					StackArgumentsCallHandle icmh = (StackArgumentsCallHandle) memberHandle;
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
		private boolean isCovariantMethod(List<CallHandle> calls) {
			if (!(this.declaration instanceof MethodDeclarationHandle)) {
				return false;
			}
			
			if ((calls.size() == 1) && (calls.get(0) instanceof MethodDeclarationHandle)) {
				MethodDeclarationHandle theCall = (MethodDeclarationHandle) calls.get(0);
				if (theCall.getName().equals(this.declaration.getName())) {
					MethodDeclarationHandle fromMethod = ((MethodDeclarationHandle) this.declaration);
					if (theCall.getGenericType().isAssignableFrom(fromMethod.getGenericType())) {
						if (theCall.getGenericTypes().length == fromMethod.getGenericTypes().length) {
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
	private boolean checkInterface;
	private boolean checkImpl;

	public PureChecklistHandler(ClassLoader cl, ClassAnnotationCache immutables, ClassAnnotationCache mutableUnshared, boolean intf, boolean impl) {
		super();
		this.cl = cl;
		this.immutables = immutables;
		this.mutableUnshared = mutableUnshared;
		this.checkInterface = intf;
		this.checkImpl = impl;
		loadPureLists();
	}

	private Map<DeclarationHandle, PureMethod> pureChecklist = new HashMap<DeclarationHandle, PureMethod>();

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
			Enforcement impl = Enforcement.valueOf(parts[0]);
			PureMethod pureMethod = new PureMethod(parts[1], impl, null);
			pureChecklist.put(pureMethod.declaration, pureMethod);
			line = br.readLine();
		}
	}

	private static boolean isAnonymousInnerClass(String className) {
		String tail = className.substring(className.lastIndexOf("$")+1);
		boolean out = tail.matches("[0-9]*");
		//int mods = PureChecklistHandler.class.getModifiers();
		return out;
	}
	
	
	public boolean isMarkedPure(DeclarationHandle mh, Callback cb, ProjectModel pm) {
		boolean staticMethod = false;
		if (mh instanceof MethodDeclarationHandle) {
			staticMethod = mh.getName().equals("<clinit>") || mh.isStatic();
		}
		
		if (pureChecklist.containsKey(mh)) {
			PureMethod purem = pureChecklist.get(mh);
			return purem.implPurity != Enforcement.NOT_PURE;
		}

		if (IGNORE_EQUALS_PARAMETER_PURITY) {
			if (("equals".equals(mh.getName())) && ("(Ljava/lang/Object;)Z".equals(mh.getDesc()))) {
				return true;
			}
		}
		
		if ((mh instanceof ConstructorDeclarationHandle) && (isAnonymousInnerClass(mh.getClassName()))) {
			// because you can't add the pure annotation to anonymous inner classes, we assume it is pure.
			return true;
		}

		AnnotationHandle p = mh.getAnnotation(Pure.class);
		if (p != null) {
			return p.getField("value") != Enforcement.NOT_PURE;
		}

		if (staticMethod) {
			if (isInnerClassAccessMethod(mh, cb, pm)) {
				return true;
			}
			
			return false;
		}

		// interfaces now allowed as pure
		if (mh.getDeclaringClass().isInterface()) {
			return true;
		}

		if (isMarkedPure(mh.getDeclaringClass(), cb, pm)) {
			return true;
		}

		if (IGNORE_EXCEPTION_CONSTRUCTION) {
			if (mh.getDeclaringClass().isThrowable()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Normalize so that we are calling the class where the method is declared.
	 */
	private CallHandle ensureCorrectClass(CallHandle mh) {
//		if (mh instanceof MethodHandle) {
//			Method m = ((MethodHandle)mh).hydrate(cl);
//			MethodHandle m2 = new MethodHandle(m);
//			return m2;
//		}
		System.out.println("ALERT: ensureCorrectClass");
		return mh;
	}

	private boolean isInnerClassAccessMethod(DeclarationHandle mh, Callback cb, ProjectModel pm) {
		if (mh instanceof MethodDeclarationHandle) {
			if ((mh.getClassName().startsWith("access$")) && 
					(((MethodDeclarationHandle)mh).getGenericTypes().length >= 1)) {
				
				GenericTypeHandle t = ((MethodDeclarationHandle) mh).getGenericTypes()[0];
				if (t instanceof ClassHandle) {
					return isMarkedPure((ClassHandle) t, cb, pm);
				}
			}
		}	
		return false;
	}
	
	public boolean isMarkedPure(ClassHandle in, Callback cb, ProjectModel pm) {
		if ((in == null) || (in.isObject())) {
			return false;
		}

		if (immutables.classIsMarked(in, cb, pm)) {
			return true;
		}
		
		if (mutableUnshared.classIsMarked(in, cb, pm)) {
			return true;
		}
		
		AnnotationHandle p = in.getAnnotation(Pure.class);
		if ((p != null) && (p.getField("value") != Enforcement.NOT_PURE)) {
			return true;
		}

		for (String intf : in.getInterfaces()) {
			if (isMarkedPure(pm.getClassHandle(intf), cb, pm)) {
				return true;
			}
		}

		if (isMarkedPure(pm.getClassHandle(in.getSuperclass()), cb, pm)) {
			return true;
		}

		return false;
	}

	public void addMethod(DeclarationHandle declaration, Enforcement impl, Enforcement intf, PurityType pt, ClassHandle usedIn, Callback cb) {
		PureMethod pm;
		if (pureChecklist.containsKey(declaration)) {
			pm = pureChecklist.get(declaration);
		} else {
			pm = new PureMethod(declaration, impl, intf, pt);
			pureChecklist.put(declaration, pm);
			cb.send("  - " + declaration + " " + impl);
		}

		if (pm.implPurity != impl) {
			if (pm.implPurity != Enforcement.FORCE) {
				cb.registerError(new ClassExpectingPureMethod(usedIn, pm));
			}
		}
		
		if (pm.implPurity != Enforcement.NOT_PURE) {
			if ((pm.pt != pt) && (pm.pt != null)) {
				cb.registerError(new ClassHierarchyUsesBothTypesOfPurity(pm, usedIn));
			}
		}

		pm.usedIn.add(usedIn);
	}

	public PureMethod getElementFor(CallHandle mh) {
		return pureChecklist.get(mh);
	}

	public void doPureMethodChecks(Callback cb, ProjectModel pm) {
		for (PureMethod pureCandidate : pureChecklist.values()) {
			if (checkImpl) {
				pureCandidate.checkImplementationPurity(cb, pm);
			}
			if (checkInterface) {
				pureCandidate.checkInterfacePurity(cb, pm);
			}
		}
	}

	public Collection<PureMethod> getMethodList() {
		return pureChecklist.values();
	}

	public static boolean isAccessibleOutsideClass(DeclarationHandle handle, ClassLoader cl, ProjectModel pm) {
		boolean pub = handle.isPublic();
		boolean priv = handle.isPrivate();
		boolean prot = handle.isProtected();
		boolean synthetic = false;
		CallInfo ci = pm.getOpcodes(handle);
		synthetic = (Opcodes.ACC_SYNTHETIC & ci.getOpcodes()) == Opcodes.ACC_SYNTHETIC;
		if (synthetic) {
			return false;
		}
		
		if (pm.getClassHandle(handle).isAnonymousInner()) {
			return false;
		}
		
		return pub || ((!priv) && (!prot));
	}
}
