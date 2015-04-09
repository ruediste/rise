package com.github.ruediste.laf.core.classReload;

import static java.util.stream.Collectors.toList;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.tree.ClassNode;

import com.github.ruediste.laf.core.classReload.ClassChangeNotifier.ClassChangeTransaction;
import com.google.common.collect.Iterables;

@Singleton
public class ClassHierarchyCache {
	@Inject
	ClassChangeNotifier notifier;

	@PostConstruct
	public void setup() {
		notifier.addPreListener(this::onChange);
	}

	public static class Parents {
		public final String superName;
		public final List<String> interfaces;

		public Parents(String superName, List<String> interfaces) {
			super();
			this.superName = superName;
			this.interfaces = interfaces;
		}

	}

	private Map<String, Parents> classMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	private void onChange(ClassChangeTransaction trx) {
		for (String name : trx.removedClasses) {
			classMap.remove(toName(name));
		}
		for (ClassNode cls : Iterables.concat(trx.modifiedClasses,
				trx.addedClasses)) {
			classMap.put(toName(cls.name), new Parents(toName(cls.superName),
					toNames(cls.interfaces)));
		}
	}

	private List<String> toNames(List<String> interfaces) {
		if (interfaces == null) {
			return null;
		}
		return interfaces.stream().map(this::toName).collect(toList());
	}

	private String toName(String internalName) {
		return internalName.replace('/', '.');
	}

	public Parents getParents(String name) {
		return classMap.get(name);
	}
}
