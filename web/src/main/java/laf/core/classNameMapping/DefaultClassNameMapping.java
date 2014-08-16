package laf.core.classNameMapping;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.*;
import com.google.common.collect.Iterables;

public class DefaultClassNameMapping implements Function<Class<?>, String> {

	final private String basePackage;
	final private String suffix;

	public DefaultClassNameMapping(String basePackage, String suffix) {
		this.basePackage = basePackage;
		this.suffix = suffix;
	}

	@Override
	public String apply(Class<?> cls) {
		String name = cls.getName();

		// remove the base package
		if (!Strings.isNullOrEmpty(basePackage)) {
			if (name.startsWith(basePackage + ".")) {
				name = name.substring(basePackage.length() - 1);
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
		name = Joiner.on('.').join(parts);
		return name;
	}

}
