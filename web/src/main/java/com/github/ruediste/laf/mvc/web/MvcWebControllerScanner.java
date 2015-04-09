package com.github.ruediste.laf.mvc.web;

import javax.inject.Inject;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.classReload.ClassHierarchyCache;
import com.github.ruediste.laf.core.defaultConfiguration.DefaultConfiguration;

public class MvcWebControllerScanner {

	@Inject
	DefaultConfiguration coreConfig;

	@Inject
	ClassHierarchyCache cache;

	public void registerControllers() {
		String internalName = Type.getInternalName(ControllerMvcWeb.class);
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
