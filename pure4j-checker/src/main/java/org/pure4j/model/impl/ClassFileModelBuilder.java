package org.pure4j.model.impl;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Stack;

import org.pure4j.Pure4J;
import org.pure4j.exception.Pure4JException;
import org.pure4j.model.CallHandle;
import org.pure4j.model.CallInfo;
import org.pure4j.model.DeclarationHandle;
import org.pure4j.model.GenericTypeHandle;
import org.pure4j.model.impl.ClassHandle.Classification;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.core.io.Resource;


/**
 * Handles visiting class file resources and adding the details to the model.
 * 
 * @author robmoffat
 * 
 */
public class ClassFileModelBuilder {
	
	public static boolean ALWAYS_OUTPUT_ASM = true;

	public ClassFileModelBuilder() {
		this(ALWAYS_OUTPUT_ASM);
	}
	
	public ClassFileModelBuilder(boolean output) {
		this.output = ALWAYS_OUTPUT_ASM;
	}
	

	ProjectModelImpl model = new ProjectModelImpl();

	public ProjectModelImpl getModel() {
		return model;
	}

	public void visit(Resource resource) throws IOException {
		visit(resource.getInputStream());
	}
	
	public void visit(InputStream is) throws IOException {
		ClassReader cr = new ClassReader(is);
		cr.accept(createClassVisitor(model), 0);
	}

	protected String convertAnnotationDescriptor(String desc) {
		Type t = Type.getType(desc);
		return fixClassName(t.getInternalName());
	}
	
	protected String fixClassName(String name) {
		return name.replace("/", ".");
	}
	
	protected String[] fixClassName(String[] name) {
		for (int i = 0; i < name.length; i++) {
			name[i] = fixClassName(name[i]);
		}
		return name;
	}


	private ClassVisitor createClassVisitor(final ProjectModelImpl model) {
		return new ClassVisitor(Opcodes.ASM5) {
			
			ClassHandle ch; 
 
			public void visit(int version, int access, String name, String sig, String superName, String[] interfaces) {
				output("CLASS: "+name);
				String packageName = getPackageName(name);
				name = fixClassName(name);
				superName = fixClassName(superName);
				model.addSubclass(superName, name);

				model.addPackageClass(packageName, name);
				ch = new ClassHandle(name, superName, fixClassName(interfaces), access, null);
				model.addClass(name, ch);

				for (int j = 0; j < interfaces.length; j++) {
					model.addSubclass(interfaces[j], name);
					addDependency(name, model, interfaces[j]);
				}
				
				addDependency(name, model, superName);
			}

		
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				String annotationClass = convertAnnotationDescriptor(desc);
				AnnotationHandle ah = new AnnotationHandle(ch, annotationClass);
				ch.addHandle(ah);
				model.addClassAnnotation(annotationClass, ch.getClassName());
				addDependency(ch.getClassName(), model, desc, false);
				return createAnnotationVisitor(model, ah, desc);
			}

			public FieldVisitor visitField(int access, String name, String desc, String sign, Object value) {
				GenericTypeHandle gth = createGenericType(desc);
				final FieldDeclarationHandle mh = new FieldDeclarationHandle(ch.getClassName(), name, 0, access, ch, gth);
				addDependency(ch.getClassName(), model, desc, true);
				ch.fields.add(mh);
				return createFieldVisitor(model, ch.getClassName(), mh);
			}

			public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
				final DeclarationHandle mh = createHandle(ch.getClassName(), name, desc, 0, access, ch);
				CallInfo ci = new CallInfo();
				ci.setOpcodes(access);
				model.setOpcodes(mh, ci);
				addDependency(ch.getClassName(), model, desc, true);
				return createMethodVisitor(model, ch.getClassName(), mh, ch.getSuperclass(), ci);

			}

			public void visitOuterClass(String arg0, String arg1, String arg2) {
			}

			public void visitSource(String arg0, String arg1) {
			}

			public void visitAttribute(Attribute arg0) {
			}

			public void visitEnd() {
			}

			public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
			}
		};
	}

	private FieldVisitor createFieldVisitor(final ProjectModelImpl model, final String className, final FieldDeclarationHandle mh) {
		return new FieldVisitor(Opcodes.ASM5) {

			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				String annotationClass = convertAnnotationDescriptor(desc);
				AnnotationHandle ah = new AnnotationHandle(mh, annotationClass);
				mh.addHandle(ah);
				model.addMemberAnnotation(annotationClass, mh);
				return createAnnotationVisitor(model, ah, desc);
			}

			public void visitAttribute(Attribute arg0) {
			}

			public void visitEnd() {
			}

		};
	}
	
	public static String getOpcode(int in) {
		for (Field f : Opcodes.class.getDeclaredFields()) {
			int value;
			try {
				if (f.getType() == int.class) {
					value = f.getInt(null);
					
					if ((value == in) 
							&& (f.getName().charAt(1)!='_')// not opcodes, other flags
							&& (!f.getName().startsWith("ACC_"))) { 
						return f.getName();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return in+"(unknown)";
		
	}

	private AnnotationVisitor createAnnotationVisitor(final ProjectModelImpl model, final AnnotationHandle ah, final String desc) {
		return new AnnotationVisitor(Opcodes.ASM5) {

			public void visit(String arg0, Object arg1) {
				if (arg1 instanceof Type) {
					ah.addField(arg0 == null ? "value" : arg0, createGenericType((Type) arg1));
					Type referenced = (Type) arg1;
					String name = referenced.getClassName();
					model.addAnnotationReference(fixClassName(name), ah);
					addDependency(ah.getAnnotatedItem().getDeclaringClass().getClassName(), model, referenced);
				} else {
					ah.addField(arg0 == null ? "value" : arg0, arg1);
				}
			}

			public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
				return null;
			}

			public AnnotationVisitor visitArray(final String arg0) {
				return this;
			}


			public void visitEnd() {
			}

			public void visitEnum(String arg0, String arg1, String arg2) {
			}

		};
	}

	private MethodVisitor createMethodVisitor(final ProjectModelImpl model, final String className, final DeclarationHandle mh, final String superName, final CallInfo ci) {
		model.addDeclaredMethod(className, mh);
		final String methodName = mh.getName();
		output(methodName);
		
		return new MethodVisitor(Opcodes.ASM5) {

			Stack<Integer> arguments = new Stack<Integer>();
			int line = 0;
			boolean firstCall = true;
			CallHandle lastMethodCall;

			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				String annotationClass = convertAnnotationDescriptor(desc);
				AnnotationHandle ah = new AnnotationHandle(mh, annotationClass);
				((AbstractAnnotatedHandle)mh).addHandle(ah);
				model.addMemberAnnotation(annotationClass, mh);
				return createAnnotationVisitor(model, ah, desc);
			}

			public void visitFieldInsn(int arg0, String owner, String name, String desc) {
				FieldCallHandle field = new FieldCallHandle(owner, name, desc, line);
				model.addCalls(mh, field);
				addDependency(className, model, desc, true);
				model.addClassDependency(className, owner);
				output("  "+getOpcode(arg0)+" "+owner+" "+name+" "+desc);
				resetCallDetails();
			}

			public void visitMethodInsn(int arg0, String owner, String name, String desc, boolean itf) {
				//String callerName = methodName;
				CallHandle remoteMethod = null;
				if (name.equals("<init>")) {
					remoteMethod = new StackArgumentsConstructorCallHandle(owner, desc, line, arguments, true);	
					arguments = new Stack<Integer>();
					
					// check calling the superclass constructor:  Reactivate first call for possible immutable after it.
					firstCall = mh.getName().equals("<init>") && owner.equals(superName);
				} else {
					remoteMethod = new StackArgumentsMethodCallHandle(owner, name, desc, line, arguments, firstCall);
					arguments = new Stack<Integer>();
					
					if (!owner.equals(Type.getInternalName(Pure4J.class))) {
						firstCall = false;
					}
				}

				model.addCalls(mh, remoteMethod);
				addDependency(className, model, desc, true);
				model.addClassDependency(className, owner);
				lastMethodCall = remoteMethod;
				output("  "+getOpcode(arg0)+" "+owner+" "+name+" "+desc);
			}

			public void visitTypeInsn(int arg0, String type) {
				model.addClassDependency(className, type);
				output("  "+getOpcode(arg0)+" "+type);
				if (arg0 != Opcodes.CHECKCAST) {
					arguments = new Stack<Integer>();
					lastMethodCall = null;
				}
			}

			public void visitMultiANewArrayInsn(String desc, int arg1) {
				addDependency(className, model, desc, false);
				output("  Multi New Array"+desc+" "+arg1);
				resetCallDetails();
			}

			/**
			 * TODO: add support for parameter annotations
			 */
			public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
				return null;
			}

			public void visitVarInsn(int arg0, int arg1) {
				output("  "+getOpcode(arg0)+" "+arg1);
				if (Opcodes.ALOAD == arg0) {
					arguments.push(arg1);
					
					if (arg1 == 0) {
						ci.setUsesThis(true);
					}
					
				} else {
					firstCall = false;
				}
			}

			public AnnotationVisitor visitAnnotationDefault() {
				return null;
			}

			public void visitAttribute(Attribute arg0) {
			}

			public void visitCode() {
			}

			public void visitEnd() {
			}

			public void visitIincInsn(int arg0, int arg1) {
				output("  "+getOpcode(arg0)+ " "+arg1);
				resetCallDetails();
			}

			public void visitInsn(int arg0) {
				output("  "+getOpcode(arg0));
				
				if (Opcodes.ARETURN == arg0) {
					int line2 = line;
					// we are returning something.  we need to keep track of the previous
					ci.addMethodBeforeReturn(lastMethodCall == null ? line2 : lastMethodCall);
				}
			}

			public void visitIntInsn(int arg0, int arg1) {
				output("  "+getOpcode(arg0)+ " "+arg1);
				resetCallDetails();
			}

			public void visitJumpInsn(int arg0, Label arg1) {
				resetCallDetails();
			}

			public void visitLabel(Label arg0) {
			}

			public void visitLdcInsn(Object arg0) {
				output("  "+arg0);
				resetCallDetails();
			}

			public void visitLineNumber(int arg0, Label arg1) {
				output("line: "+arg0);
				line = arg0;
			}

			public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {
				output("  visitLocalVariable "+arg0+" "+arg1+" "+arg2+" "+arg3+" "+arg4+" "+arg5);
				resetCallDetails();
			}

			public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
				output("  lookupswitch");
				resetCallDetails();
			}

			public void visitMaxs(int arg0, int arg1) {
				output("  visitMaxs "+arg0+" "+arg1);
				resetCallDetails();
			}

			public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label... arg3) {
				output("  lookupswitch");
				resetCallDetails();
			}

			protected void resetCallDetails() {
				firstCall = false;
				arguments = new Stack<Integer>();
				lastMethodCall = null;
			}

			public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
				output("  try/catch: "+arg0+" "+arg1+" "+arg2+" "+arg3);
				//resetCallDetails();
			}

		};
	}
	
	private boolean output;

	private void output(String name) {
		if (output) {
			System.out.println(name);
		}
	}

	private void addDependency(final String className, final ProjectModelImpl model, String desc, boolean returnType) {
			Type t = returnType ? Type.getReturnType(desc) : Type.getType(desc);
			addDependency(className, model, t);
	}
	
	private void addDependency(final String className, final ProjectModelImpl model, Type t) {
		try {
			if (t.getSort() == Type.ARRAY) {
				t = t.getElementType();
			}

			if (t.getSort() == Type.OBJECT) {
				String className2 = t.getInternalName();
				addDependency(className, model, className2);
			}
		} catch (RuntimeException e) {
			throw new Pure4JException("Could not handle type: " + t.getDescriptor(), e);
		}
	}

	private void addDependency(final String className, final ProjectModelImpl model, String className2) {
		model.addClassDependency(className, className2);
	}

	private String getPackageName(String name) {
		int li = name.lastIndexOf("/");
		li = li==-1 ? 0 : li;
		return name.substring(0, li);
	}
	
	private DeclarationHandle createHandle(String owner, String name, String desc, int line, int modifiers, ClassHandle ch) {
		if (name.equals("<init>")) {
			ConstructorDeclarationHandle constructorDeclarationHandle = 
					new ConstructorDeclarationHandle(owner, desc, line, modifiers, ch, createArguments(desc));
			ch.constructors.add(constructorDeclarationHandle);
			return constructorDeclarationHandle;	
			
		} else if (name.equals("<clinit>")) {
			return new ClassInitHandle(owner, desc, line, modifiers, ch);
			
		} else {
			MethodDeclarationHandle methodDeclarationHandle = 
					new MethodDeclarationHandle(owner, name, desc, line, modifiers, ch, createGenericType(desc), createArguments(desc));
			ch.methods.add(methodDeclarationHandle);
			return methodDeclarationHandle;
		}
	}
	
	private GenericTypeHandle[] createArguments(String desc) {
		Type[] t = Type.getArgumentTypes(desc);
		GenericTypeHandle[] out = new GenericTypeHandle[t.length];
		for (int i = 0; i < out.length; i++) {
			out[i] = createGenericType(t[i]);
		}

		return out;
	}

 	private GenericTypeHandle createGenericType(String desc) {
		Type t = Type.getReturnType(desc);
		return createGenericType(t);
	}

	
	private GenericTypeHandle createGenericType(Type t) {
		if (t.getSort() == Type.ARRAY) {
			return new ArrayHandle(t.getDimensions(), createGenericType(t.getElementType()));
		} else if (t.getSort() == Type.METHOD) {
			throw new RuntimeException("not yet supported");
		} else if (t.getSort() == Type.OBJECT) {
			return new ClassHandle(t.getClassName(), null, null, 0, Classification.CLASS);
		} else {
			return new ClassHandle(t.getClassName(), null, null, 0, Classification.PRIMITIVE);
		}
	}

}
