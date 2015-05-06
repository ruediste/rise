package com.github.ruediste.laf.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Map a class to it's fully qualified name. If a base package is specified it
 * is removed from the result. If a suffix is specified, it is removed from the
 * result. The class name is transformed to lower camel form.
 */
public class DefaultClassNameMapping implements Function<ClassNode, String> {

	private String basePackage;
	private String suffix;

	/**
	 * Initialize mapping. Arguments can be null.
	 */
	public void initialize(String basePackage, String suffix) {
		this.basePackage = basePackage;
		this.suffix = suffix;
	}

	@Override
	public String apply(ClassNode cls) {
		String name = Type.getObjectType(cls.name).getClassName();

		// remove the base package
		if (!Strings.isNullOrEmpty(basePackage)) {
			if (name.startsWith(basePackage + ".")) {
				name = name.substring(basePackage.length() + 1);
			}
		}

		// remove a suffix, if present
		if (!Strings.isNullOrEmpty(suffix)) {
			if (name.endsWith(suffix)) {
				name = name.substring(0, name.length() - suffix.length());
			}
		}

		// lowercamelize the class name
		List<String> parts = new ArrayList<>();
		Iterables.addAll(parts, Splitter.on('.').split(name));
		parts.set(
				parts.size() - 1,
				CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						parts.get(parts.size() - 1)));
		name = Joiner.on('/').join(parts);
		return name;
	}

}
