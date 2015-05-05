package com.github.ruediste.laf.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.inject.Inject;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.mvc.Updating;

public class ControllerReflectionUtil {

	@Inject
	ClassHierarchyCache cache;

	public boolean isActionMethod(Method method) {
		return Modifier.isPublic(method.getModifiers())
				&& (ActionResult.class.isAssignableFrom(method.getReturnType()));
	}

	public boolean isActionMethod(MethodNode method) {
		boolean isPub = (Opcodes.ACC_PUBLIC & method.access) != 0;
		Type returnType = Type.getReturnType(method.desc);
		boolean returnsActionResult = Type.getType(ActionResult.class).equals(
				returnType);

		return isPub && (returnsActionResult);
	}

	public boolean isUpdating(Method method) {
		return method.isAnnotationPresent(Updating.class);
	}
}
