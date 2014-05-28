package laf.httpRequestMapping.defaultRule;

import java.util.List;

import javax.inject.Inject;

import laf.configuration.ConfigurationValue;
import laf.controllerInfo.ControllerInfo;

import com.google.common.base.*;
import com.google.common.collect.Lists;

/**
 * Generates a controller identifier form a controller class name by removing
 * the basePackage prefix, removing an eventual Controller suffix of the class
 * name, and removing an eventual controller subpackage. The packages of the
 * identifier are separated with a forward slash.
 */
public class DefaultControllerIdentifierStrategy implements
		ControllerIdentifierStrategy {

	@Inject
	ConfigurationValue<BasePackage> basePackage;

	public String getControllerIdentifier(String controllerClassName) {
		List<String> parts = getControllerIdentifierParts(controllerClassName);

		// lowercamelize the controller name, which is last in the parts
		parts.set(
				parts.size() - 1,
				CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						parts.get(parts.size() - 1)));

		// join the parts
		return Joiner.on("/").join(parts);

	}

	public List<String> getControllerIdentifierParts(String controllerClassName) {
		String name = controllerClassName;

		if (!Strings.isNullOrEmpty(basePackage.value().get())
				&& name.startsWith(basePackage.value().get() + ".")) {
			// remove package
			name = controllerClassName
					.substring((basePackage.value().get() + ".").length());

		}

		// remove Controller suffix
		if (name.endsWith("Controller")) {
			name = name.substring(0, name.length() - "Controller".length());
		}

		// split the name
		List<String> parts = Lists.newArrayList(Splitter.on(".").split(name));

		// remove a controller subpackage
		if (parts.size() >= 2) {
			if (parts.get(parts.size() - 2).equals("controller")) {
				parts.remove(parts.size() - 2);
			}
		}
		return parts;
	}

	@Override
	public String apply(ControllerInfo info) {
		return getControllerIdentifier(info.getQualifiedName());
	}

}
