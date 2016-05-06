/*
 * Copyright TrigerSoft <kostat@trigersoft.com> 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.ruediste.rise.lambda.expression;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Represents a visitor or rewriter for expression trees.
 * 
 * @author <a href="mailto://kostat@trigersoft.com">Konstantin Triger</a>
 */

final class ExpressionClassVisitor extends ClassVisitor {

	private final String _methodName;
	private final String _methodDesc;

	private Expression _result;
	private Class<?> returnType;
	private Class<?>[] parameterTypes;
	private ClassLoader classLoader;
	private Class<?> ownerType;

	Expression getResult() {
		return _result;
	}

	void setResult(Expression result) {
		_result = result;
	}

	Class<?> getReturnType() {
		return returnType;
	}

	Class<?>[] getParameterTypes() {

		return parameterTypes;
	}

	public ExpressionClassVisitor(String methodName, String methodDescriptor, ClassLoader classLoader) {
		super(Opcodes.ASM5);
		this.classLoader = classLoader;
		_methodName = methodName;
		_methodDesc = methodDescriptor;
	}

	Class<?> getClass(Type t) {
		try {
			switch (t.getSort()) {
			case Type.BOOLEAN:
				return Boolean.TYPE;
			case Type.CHAR:
				return Character.TYPE;
			case Type.BYTE:
				return Byte.TYPE;
			case Type.SHORT:
				return Short.TYPE;
			case Type.INT:
				return Integer.TYPE;
			case Type.FLOAT:
				return Float.TYPE;
			case Type.LONG:
				return Long.TYPE;
			case Type.DOUBLE:
				return Double.TYPE;
			case Type.VOID:
				return Void.TYPE;
			}
			String cn = t.getInternalName();
			cn = cn != null ? cn.replace('/', '.') : t.getClassName();

			return Class.forName(cn, false, classLoader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		ownerType = getClass(Type.getObjectType(name));
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		if (!_methodName.equals(name) || !_methodDesc.equals(desc))
			return null;

		Type ret = Type.getReturnType(desc);
		boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;

		returnType = getClass(ret);

		Type[] argTypes = Type.getArgumentTypes(desc);
		Class<?>[] initialLocalVariableTypes = new Class<?>[isStatic ? argTypes.length : argTypes.length + 1];

		if (!isStatic) {
			initialLocalVariableTypes[0] = ownerType;
		}

		parameterTypes=new Class<?>[argTypes.length];
		for (int i = 0; i < argTypes.length; i++)
		{
			Class<?> tmp = getClass(argTypes[i]);
			parameterTypes[i]=tmp;
			initialLocalVariableTypes[isStatic ? i : i + 1] = tmp;
		}
		return new ExpressionMethodVisitor(this, initialLocalVariableTypes);
	}

}
