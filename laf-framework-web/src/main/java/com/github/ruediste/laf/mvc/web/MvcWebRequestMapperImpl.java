package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.inject.Inject;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.util.Pair;

public class MvcWebRequestMapperImpl {

	@Inject
	CoreConfiguration coreConfig;

	@Inject
	ClassHierarchyCache cache;

	@Inject
	PathInfoIndex idx;

	private HashMap<Pair<Class<?>, Method>, String> methodToPathInfoMap = new HashMap<>();

	public void registerControllers() {
		String internalName = Type.getInternalName(IControllerMvcWeb.class);
		registerControllers(internalName);
	}

	private void registerControllers(String internalName) {
		for (ClassNode child : cache.getChildren(internalName)) {
			register(child);
			registerControllers(child.name);
		}
	}

	private void register(ClassNode cls) {

	}
}
