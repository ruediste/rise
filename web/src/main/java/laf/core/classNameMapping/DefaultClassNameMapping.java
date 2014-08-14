package laf.core.classNameMapping;

import com.google.common.base.Strings;

public class DefaultClassNameMapping implements ClassNameMapping {

	final private String basePackage;
	final private String suffix;

	public DefaultClassNameMapping(String basePackage, String suffix) {
		this.basePackage = basePackage;
		this.suffix = suffix;
	}

	@Override
	public String mapName(Class<?> cls) {
		String name = cls.getName();
		if (!Strings.isNullOrEmpty(basePackage)) {
			if (name.startsWith(basePackage + ".")) {
				name = name.substring(basePackage.length() - 1);
			}
		}

		if (!Strings.isNullOrEmpty(suffix)) {
			if (name.endsWith(suffix)) {
				name = name.substring(0, name.length() - suffix.length());
			}
		}
		return name;
	}

}
