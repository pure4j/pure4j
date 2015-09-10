package org.pure4j.processor;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Stack;

import org.pure4j.Pure4J;
import org.pure4j.model.AnnotatedElementHandle;
import org.pure4j.model.AnnotationHandle;
import org.pure4j.model.ClassHandle;
import org.pure4j.model.ConstructorHandle;
import org.pure4j.model.FieldHandle;
import org.pure4j.model.ImmutableCallMemberHandle;
import org.pure4j.model.MemberHandle;
import org.pure4j.model.MethodHandle;
import org.pure4j.model.PackageHandle;
import org.pure4j.model.ProjectModelImpl;
import org.pure4j.model.Pure4JException;
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
	
	public static final boolean OUTPUT_ASM = false;

	ProjectModelImpl model = new ProjectModelImpl();

	public ProjectModelImpl getModel() {
		return model;
	}

	public void visit(Resource resource) throws IOException {
		ClassReader cr = new ClassReader(resource.getInputStream());
		cr.accept(createClassVisitor(model), 0);
	}

	protected String convertAnnotationDescriptor(String desc) {
		Type t = Type.getType(desc);
		return t.getInternalName();
	}

	public ClassVisitor createClassVisitor(final ProjectModelImpl model) {
		return new ClassVisitor(Opcodes.ASM4) {

			String className;

			public void visit(int version, int access, String name, String sig, String superName, String[] interfaces) {
				output("CLASS: "+name);
				this.className = name;
				model.addSubclass(superName, name);

				String packageName = getPackageName(name);
				model.addPackageClass(packageName, name);
				model.addClass(name);

				for (int j = 0; j < interfaces.length; j++) {
					model.addSubclass(interfaces[j], name);
					addDependency(name, model, interfaces[j]);
				}
				
				addDependency(name, model, superName);
			}

			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				model.addClassAnnotation(convertAnnotationDescriptor(desc), className);
				addDependency(className, model, desc, false);
				return createAnnotationVisitor(model, new ClassHandle(className), desc);
			}

			public FieldVisitor visitField(int access, String name, String desc, String sign, Object value) {
				final FieldHandle mh = new FieldHandle(className, name);
				addDependency(className, model, desc, true);
				return createFieldVisitor(model, className, mh);
			}

			public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
				final MemberHandle mh = createHandle(className, name, desc, 0);
				addDependency(className, model, desc, true);
				return createMethodVisitor(model, className, mh);

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

	private FieldVisitor createFieldVisitor(final ProjectModelImpl model, final String className, final FieldHandle mh) {
		return new FieldVisitor(Opcodes.ASM4) {

			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				model.addMemberAnnotation(convertAnnotationDescriptor(desc), mh);
				return createAnnotationVisitor(model, mh, desc);
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
							&& (f.getName().charAt(1)!='_')) { // not opcodes, other flags
						return f.getName();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return in+"(unknown)";
		
	}

	private AnnotationVisitor createAnnotationVisitor(final ProjectModelImpl model,
			final AnnotatedElementHandle<?> handle, final String desc) {
		return new AnnotationVisitor(Opcodes.ASM4) {

			String field = null;

			public void visit(String arg0, Object arg1) {
				setFieldName(arg0);
				if (arg1 instanceof Type) {
					Type referenced = (Type) arg1;
					String name = referenced.getClassName();
					Type annotationClass = Type.getType(desc);
					AnnotationHandle ah = new AnnotationHandle(field, handle, MemberHandle
							.convertClassName(annotationClass.getClassName()));
					model.addAnnotationReference(MemberHandle.convertClassName(name), ah);
					addDependency(handle.getDeclaringClass(), model, referenced);
				}
			}

			public AnnotationVisitor visitAnnotation(String arg0, String arg1) {
				return null;
			}

			public AnnotationVisitor visitArray(final String arg0) {
				setFieldName(arg0);
				return this;
			}

			private void setFieldName(final String arg0) {
				if (arg0 != null)
					field = arg0;
			}

			public void visitEnd() {
			}

			public void visitEnum(String arg0, String arg1, String arg2) {
			}

		};
	}

	private MethodVisitor createMethodVisitor(final ProjectModelImpl model, final String className,
			final MemberHandle mh) {
		
		output(mh.getName());
		
		return new MethodVisitor(Opcodes.ASM4) {

			Stack<Integer> arguments = new Stack<Integer>();
			int line = 0;
			boolean firstCall = true;

			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				model.addMemberAnnotation(convertAnnotationDescriptor(desc), mh);
				return createAnnotationVisitor(model, mh, desc);
			}

			public void visitFieldInsn(int arg0, String owner, String name, String desc) {
				FieldHandle field = new FieldHandle(owner, name);
				model.addCalls(mh, field);
				addDependency(className, model, desc, true);
				model.addClassDependency(className, owner);
				output("  "+getOpcode(arg0)+" "+owner+" "+name+" "+desc);
			}

			public void visitMethodInsn(int arg0, String owner, String name, String desc) {
				MemberHandle remoteMethod = null;
				if (owner.equals(Type.getInternalName(Pure4J.class)) && (name.equals("immutable"))) {
					remoteMethod = new ImmutableCallMemberHandle(owner, name, desc, line, arguments, firstCall);
					arguments = new Stack<Integer>();
				} else {
					remoteMethod = createHandle(owner, name, desc, line);	
					firstCall = false;
				}
				model.addCalls(mh, remoteMethod);
				addDependency(className, model, desc, true);
				model.addClassDependency(className, owner);
				output("  "+getOpcode(arg0)+" "+owner+" "+name+" "+desc);
			}

			public void visitTypeInsn(int arg0, String type) {
				model.addClassDependency(className, type);
				output("  "+getOpcode(arg0)+" "+type);
				firstCall = false;
			}

			public void visitMultiANewArrayInsn(String desc, int arg1) {
				addDependency(className, model, desc, false);
				output("  Multi New Array"+desc+" "+arg1);
				firstCall = false;
			}

			/**
			 * TODO: add support for parameter annotations
			 */
			public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
				return null;
			}

			public void visitVarInsn(int arg0, int arg1) {
				output("  "+getOpcode(arg0)+" "+arg1);
				if ((Opcodes.ALOAD == arg0) && (firstCall)) {
					arguments.push(arg1);
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
				firstCall = false;
			}

			public void visitInsn(int arg0) {
				output("  "+getOpcode(arg0));
				firstCall = false;
			}

			public void visitIntInsn(int arg0, int arg1) {
				output("  "+getOpcode(arg0)+ " "+arg1);
				firstCall = false;
			}

			public void visitJumpInsn(int arg0, Label arg1) {
				firstCall = false;
			}

			public void visitLabel(Label arg0) {
			}

			public void visitLdcInsn(Object arg0) {
				output("  "+arg0);
				firstCall = false;
			}

			public void visitLineNumber(int arg0, Label arg1) {
				output("line: "+arg0);
			}

			public void visitLocalVariable(String arg0, String arg1, String arg2, Label arg3, Label arg4, int arg5) {
				output("  visitLocalVariable "+arg0+" "+arg1+" "+arg2+" "+arg3+" "+arg4+" "+arg5);
				firstCall = false;
			}

			public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
				output("  lookupswitch");
				firstCall = false;
			}

			public void visitMaxs(int arg0, int arg1) {
				output("  maxs "+arg0+" "+arg1);
				firstCall = false;
			}

			public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label[] arg3) {
				output("  lookupswitch");
				firstCall = false;
			}

			public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
				output("  try/catch: "+arg0+" "+arg1+" "+arg2+" "+arg3);
				firstCall = false;
			}

		};
	}

	private void output(String name) {
		if (OUTPUT_ASM) {
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
		String packageName1 = getPackageName(className);
		String packageName2 = getPackageName(className2);
		PackageHandle ph1 = new PackageHandle(packageName1, className);
		PackageHandle ph2 = new PackageHandle(packageName2, className2);
		
		model.addPackageDependency(ph1, ph2);
	}

	private String getPackageName(String name) {
		int li = name.lastIndexOf("/");
		li = li==-1 ? 0 : li;
		return name.substring(0, li);
	}
	
	private MemberHandle createHandle(String owner, String name, String desc, int line) {
		if (name.equals("<init>")) {
			return new ConstructorHandle(owner, desc, line);			
		} else {
			return new MethodHandle(owner, name, desc, line);
		}
	}

	protected int[] getArgs(String desc, Stack<Integer> argStack, int[] vars) {
		if (argStack != null) {
			int args = argCount(desc);
			vars = new int[args];
		
			for (int i = 0; i < vars.length; i++) {
				vars[i] = argStack.get(i);
			}
		}
		return vars;
	}

	private int argCount(String desc) {
		return Type.getArgumentTypes(desc).length;
	}

}
