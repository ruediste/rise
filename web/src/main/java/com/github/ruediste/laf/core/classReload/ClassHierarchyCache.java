package com.github.ruediste.laf.core.classReload;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.classReload.ClassChangeNotifier.ClassChangeTransaction;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;

/**
 * An information cache containing the whole class hierarchy as well as the
 * parsed classes.
 */
@Singleton
@PermanentSpace
public class ClassHierarchyCache {
	@Inject
	ClassChangeNotifier notifier;

	@PostConstruct
	public void setup() {
		notifier.addPreListener(this::onChange);
	}

	private Map<String, ClassNode> classMap = new HashMap<>();
	private HashMultimap<String, ClassNode> childMap = HashMultimap.create();

	private void onChange(ClassChangeTransaction trx) {
		for (String name : trx.removedClasses) {
			ClassNode cls = classMap.remove(name);
			if (cls != null) {
				if (cls.superName != null) {
					childMap.remove(cls.superName, cls);
				}
				if (cls.interfaces != null) {
					for (String iface : cls.interfaces) {
						childMap.remove(iface, cls);
					}
				}
			}

		}
		for (ClassNode cls : Iterables.concat(trx.modifiedClasses,
				trx.addedClasses)) {
			classMap.put(cls.name, cls);
			if (cls.superName != null) {
				childMap.put(cls.superName, cls);
			}
			if (cls.interfaces != null) {
				for (String iface : cls.interfaces) {
					childMap.put(iface, cls);
				}
			}
		}
	}

	/**
	 * Return the parsed class node for the given internal name
	 *
	 * @param internalName
	 * @return
	 */
	public ClassNode getNode(String internalName) {
		return classMap.get(internalName);
	}

	/**
	 * Return the child classes for the given class or interface given by it's
	 * internal name
	 *
	 * @param internalName
	 * @return
	 */
	public Set<ClassNode> getChildren(String internalName) {
		return childMap.get(internalName);
	}
}
