package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.inject.Inject;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.ruediste.laf.core.ActionResult;
import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.mvc.Updating;
import com.google.common.reflect.TypeToken;

public class MvcWebControllerReflectionUtil {

	@Inject
	ClassHierarchyCache cache;

	public boolean isEmbeddedController(Type type) {
		return isEmbeddedController(type.getInternalName());
	}

	public boolean isEmbeddedController(ClassNode cls) {
		return isEmbeddedController(cls.name);
	}

	public boolean isEmbeddedController(TypeToken<?> type) {
		return TypeToken.of(IEmbeddedControllerMvcWeb.class).isAssignableFrom(
				type);
	}

	public boolean isEmbeddedController(String internalName) {
		return cache
				.isAssignableFrom(Type.getType(IEmbeddedControllerMvcWeb.class)
						.getInternalName(), internalName);
	}

	public boolean isActionMethod(Method method) {
		return Modifier.isPublic(method.getModifiers())
				&& (ActionResult.class.isAssignableFrom(method.getReturnType()) || isEmbeddedController(TypeToken
						.of(method.getReturnType())));
	}

	public boolean isActionMethod(MethodNode method) {
		boolean isPub = (Opcodes.ACC_PUBLIC & method.access) != 0;
		Type returnType = Type.getReturnType(method.desc);
		boolean returnsActionResult = Type.getType(ActionResult.class).equals(
				returnType);

		return isPub
				&& (returnsActionResult || isEmbeddedController(returnType));
	}

	public boolean isUpdating(Method method) {
		return method.isAnnotationPresent(Updating.class);
	}
}
