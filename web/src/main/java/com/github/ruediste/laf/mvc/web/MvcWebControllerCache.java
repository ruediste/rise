package com.github.ruediste.laf.mvc.web;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.sf.cglib.asm.Type;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.classReload.*;
import com.github.ruediste.laf.core.classReload.ClassChangeNotifier.ClassChangeTransaction;
import com.google.common.collect.Iterables;

/**
 * Cache for the mvc web controller classes
 */
@Singleton
public class MvcWebControllerCache {

	@Inject
	ClassChangeNotifier notifier;

	@Inject
	ClassHierarchyCache classHierarchyCache;

	@PostConstruct
	void setup() {
		notifier.addListener(this::changeOccured);
	}

	private String controllerMvcWebInternalClassName = Type
			.getInternalName(ControllerMvcWeb.class);
	private final HashMap<String, List<AnnotationNode>> controllerClassNames = new HashMap<>();

	@SuppressWarnings("unchecked")
	private void changeOccured(ClassChangeTransaction trx) {
		for (String name : trx.removedClasses) {
			getControllerClassNames().remove(name);
		}
		for (ClassNode cls : Iterables.concat(trx.modifiedClasses,
				trx.addedClasses)) {
			boolean isController = false;
			String tmp = cls.superName;
			while (tmp != null) {
				if (controllerMvcWebInternalClassName.equals(cls.superName)) {
					isController = true;
					break;
				}
				tmp = classHierarchyCache.getSuperName(tmp);
			}
			if (isController) {
				getControllerClassNames().put(cls.name, cls.visibleAnnotations);
			}
		}
	}

	public Map<String, List<AnnotationNode>> getControllerClassNames() {
		return Collections.unmodifiableMap(controllerClassNames);
	}

}
