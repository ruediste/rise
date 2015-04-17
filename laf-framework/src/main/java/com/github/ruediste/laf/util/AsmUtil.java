package com.github.ruediste.laf.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Objects;

import org.objectweb.asm.Type;

/**
 * Utility class for the use of the ASM framework
 */
public class AsmUtil {

	private AsmUtil() {
	}

	public static Class<?> getArrayType(Class<?> componentType) {
		return Array.newInstance(componentType, 0).getClass();
	}

	public static Class<?> getArrayType(Class<?> componentType, int dimensions) {
		return Array.newInstance(componentType, new int[dimensions]).getClass();
	}

	public static Class<?> loadClass(Type type, ClassLoader loader)
			throws ClassNotFoundException {
		switch (type.getSort()) {
		case Type.VOID:
			return void.class;
		case Type.BOOLEAN:
			return boolean.class;
		case Type.CHAR:
			return char.class;
		case Type.BYTE:
			return byte.class;
		case Type.SHORT:
			return short.class;
		case Type.INT:
			return int.class;
		case Type.FLOAT:
			return float.class;
		case Type.LONG:
			return long.class;
		case Type.DOUBLE:
			return double.class;
		case Type.ARRAY:
			return getArrayType(loadClass(type.getElementType(), loader),
					type.getDimensions());
		case Type.OBJECT:
			return loader.loadClass(type.getClassName());
		default:
			return null;
		}

	}

	public static class MethodRef {
		public final String declaringClassInternalName;
		public final String methodName;
		public final String desc;

		public MethodRef(String declaringClassInternalName, String methodName,
				String desc) {
			super();
			this.declaringClassInternalName = declaringClassInternalName;
			this.methodName = methodName;
			this.desc = desc;
		}

		@Override
		public int hashCode() {
			return Objects.hash(declaringClassInternalName, methodName, desc);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MethodRef) {
				MethodRef other = (MethodRef) obj;
				return Objects.equals(declaringClassInternalName,
						other.declaringClassInternalName)
						&& Objects.equals(methodName, other.methodName)
						&& Objects.equals(desc, other.desc);
			}
			return false;
		}
	}

	public static Method loadMethod(MethodRef ref, ClassLoader loader)
			throws ReflectiveOperationException {
		return loadMethod(ref.declaringClassInternalName, ref.methodName,
				ref.desc, loader);
	}

	/**
	 * Load a method using the given class loader. The return type specified in
	 * desc is ignored.
	 */
	public static Method loadMethod(String declaringClassInternalName,
			String methodName, String desc, ClassLoader loader)
			throws ReflectiveOperationException {
		Class<?> cls = loadClass(
				Type.getObjectType(declaringClassInternalName), loader);
		Type[] argTypes = Type.getArgumentTypes(desc);
		Class<?>[] argClasses = new Class<?>[argTypes.length];
		for (int i = 0; i < argTypes.length; i++) {
			argClasses[i] = loadClass(argTypes[i], loader);
		}
		return cls.getDeclaredMethod(methodName, argClasses);
	}
}
