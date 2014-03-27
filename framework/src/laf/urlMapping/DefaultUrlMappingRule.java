package laf.urlMapping;

import java.util.*;

import javax.inject.Inject;

import laf.controllerInfo.*;

import org.slf4j.Logger;

import com.google.common.base.*;
import com.google.common.collect.Lists;

/**
 * Map URLs in the form <qualifiedControllerName
 *
 */
public class DefaultUrlMappingRule implements UrlMappingRule {

	@Inject
	Logger log;

	@Inject
	ControllerInfoRepository controllerInfoRepository;

	private final HashMap<String, ControllerInfo> controllersByPrefix = new HashMap<>();

	public String getControllerPrefix(String controllerClassName) {
		List<String> parts = getControllerPrefixParts(controllerClassName);

		// lowercamelize the controller name, which is last in the parts
		parts.set(
				parts.size() - 1,
				CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
						parts.get(parts.size() - 1)));

		// join the parts
		return "/" + Joiner.on("/").join(parts);

	}

	public List<String> getControllerPrefixParts(String controllerClassName) {
		// remove package
		String prefix = controllerClassName
				.substring((Constants.basePackage + ".").length());

		// remove Controller suffix
		if (prefix.endsWith("Controller")) {
			prefix = prefix.substring(0,
					prefix.length() - "Controller".length());
		}

		List<String> parts = Lists.newArrayList(Splitter.on(".").split(prefix));

		// remove a controller subpackage
		if (parts.size() >= 2) {
			if (parts.get(parts.size() - 2).equals("controller")) {
				parts.remove(parts.size() - 2);
			}
		}
		return parts;
	}

	@Override
	public ActionPath<ParameterValueProvider> parse(String servletPath) {
		ActionPath call = new ActionPath();
		ControllerInfo controllerEntry = findControllerEntry(servletPath);

		if (controllerEntry == null) {
			return null;
		}

		// remove the prefix and split the suffix into parts at the / characters
		String[] parts = servletPath.substring(
				controllerEntry.getQualifiedName().length()).split("/");

		if (!parts[0].startsWith(".")) {
			log.debug("unable to parse servlet path " + servletPath);
			return null;
		}

		String[] actionNames;
		actionNames = parts[0].substring(1).split("\\.");

		int i = 1;
		for (String actionName : actionNames) {
			ActionInvocation invocation = new ActionInvocation();
			ActionMethodInfo actionMethodInfo = controllerEntry
					.getActionMethodInfo(actionName);
			if (actionMethodInfo == null) {
				log.debug("no ActionMethod named " + actionName + " found");
				return null;
			}

			invocation.setMethodInfo(actionMethodInfo);

			Iterator<ParameterInfo> it = actionMethodInfo.getParameters()
					.iterator();
			for (int p = 0; it.hasNext() && i < parts.length; i++, p++) {
				ParameterInfo parameter = it.next();

				invocation.arguments.add(parts[i]);
			}
			call.add(invocation);

			if (invocation.getMethod().method.getReturnType() != String.class) {
				// update the controller entry to the embedded controller
				controllerEntry = findControllerEntry(invocation.getMethod().method
						.getReturnType());
			}
		}

		return call;
	}

	@Override
	public String generate(ActionPath<Object> path) {
		return null;
	}

	private ControllerInfo findControllerEntry(String servletPath) {
		// find the first dot in the path, which separates the controller from
		// the method
		int idx = servletPath.indexOf('.');
		if (idx < 0) {
			log.debug("No dot in servlet Path, cannot determine controller");
			return null;
		}

		// get the prefix
		String qualifiedControllerName = servletPath.substring(0, idx + 1);

		return controllerInfoRepository
				.getControllerInfo(qualifiedControllerName);
	}

}