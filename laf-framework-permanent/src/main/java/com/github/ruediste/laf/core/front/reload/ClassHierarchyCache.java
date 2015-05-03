package com.github.ruediste.laf.core.front.reload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.sf.cglib.asm.Type;

import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.laf.core.front.reload.ClassChangeNotifier.ClassChangeTransaction;
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

	@Inject
	Logger log;

	@PostConstruct
	public void setup() {
		notifier.addPreListener(this::onChange);
	}

	private Map<String, ClassNode> classMap = new HashMap<>();
	private HashMultimap<String, ClassNode> childMap = HashMultimap.create();

	void onChange(ClassChangeTransaction trx) {
		log.info("change occurred. added:" + trx.addedClasses.size()
				+ " removed:" + trx.removedClasses.size() + " modified:"
				+ trx.modifiedClasses.size());
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
			log.trace("Registring class {}", cls.name);
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
		ClassNode result = classMap.get(internalName);
		log.trace("node for " + internalName + " found: " + (result != null));
		return result;
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

	/**
	 * Checks if a type is assignable from another
	 */
	public boolean isAssignableFrom(String internalNameParent,
			String internalName) {
		HashSet<String> seen = new HashSet<>();
		return isAssignableFrom(internalNameParent, internalName, seen);
	}

	private boolean isAssignableFrom(String internalNameParent,
			String internalName, HashSet<String> seen) {
		log.trace("isAssignableFrom " + internalNameParent + " " + internalName);
		if (Objects.equals(internalName, internalNameParent))
			return true;

		if (internalName == null)
			return false;

		if (!seen.add(internalName))
			return false;

		ClassNode node = getNode(internalName);
		if (node == null)
			return false;

		if (isAssignableFrom(internalNameParent, node.superName, seen))
			return true;

		if (node.interfaces != null)
			for (String iface : node.interfaces) {
				if (isAssignableFrom(internalNameParent, iface, seen))
					return true;
			}
		return false;
	}

	public ClassNode getNode(Class<?> cls) {
		return getNode(Type.getInternalName(cls));
	}
}
