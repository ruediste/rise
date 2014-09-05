package laf.mvc.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.core.base.MethodInvocation;
import laf.core.http.request.HttpRequest;
import laf.core.http.request.HttpRequestImpl;
import laf.mvc.core.ActionPath;
import laf.mvc.core.ControllerReflectionUtil;
import laf.mvc.core.api.MController;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class DefaultHttpRequestMapper implements HttpRequestMapper {
	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	private BiMap<Class<?>, String> controllerNameMap = HashBiMap.create();

	private HashMap<Class<?>, BiMap<Method, String>> actionMethodNameMap = new HashMap<>();

	public void initialize(Function<Class<?>, String> controllerNameMapper) {

		// initialize controller name map
		MController controller = new MController() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return MController.class;
			}
		};
		for (Bean<?> bean : beanManager.getBeans(Object.class, controller)) {
			Class<?> beanClass = bean.getBeanClass();
			String controllerName = controllerNameMapper.apply(beanClass);
			log.debug("found controller " + beanClass + " -> " + controllerName);
			controllerNameMap.put(beanClass, controllerName);

			// build method name map
			BiMap<Method, String> methodNameMap = HashBiMap.create();
			for (Method m : beanClass.getMethods()) {
				if (!ControllerReflectionUtil.isActionMethod(m)) {
					continue;
				}
				String name = m.getName();
				log.debug("found action method " + name);

				// find unique name
				if (methodNameMap.inverse().containsKey(name)) {
					int i = 1;
					String tmp;
					do {
						tmp = name + "_" + i;
						i += 1;
					} while (methodNameMap.inverse().containsKey(tmp));
					name = tmp;
				}

				methodNameMap.put(m, name);
			}

			actionMethodNameMap.put(beanClass, methodNameMap);
		}

	}

	@Override
	public ActionPath<String> parse(HttpRequest request) {

		ActionPath<String> call = new ActionPath<>();
		Class<?> controllerClass = findControllerEntry(request.getPath());

		if (controllerClass == null) {
			return null;
		}

		// remove the identifier and split the suffix into parts at the /
		// characters
		String[] parts = request.getPath()
				.substring(controllerNameMap.get(controllerClass).length())
				.split("/");

		if (!parts[0].startsWith(".")) {
			log.debug("unable to parse servlet path " + request);
			return null;
		}

		String[] actionNames;
		actionNames = parts[0].substring(1).split("\\.");

		int i = 1;
		for (String actionName : actionNames) {
			Method actionMethod = actionMethodNameMap.get(controllerClass)
					.inverse().get(actionName);

			if (actionMethod == null) {
				log.debug("no ActionMethod named " + actionName + " found");
				return null;
			}

			MethodInvocation<String> invocation = new MethodInvocation<>(
					controllerClass, actionMethod);

			for (; i < parts.length; i++) {
				invocation.getArguments().add(parts[i]);
			}
			call.getElements().add(invocation);

			if (ControllerReflectionUtil.isEmbeddedController(actionMethod
					.getReturnType())) {
				// update the controller class to the embedded controller
				controllerClass = actionMethod.getReturnType();
			}
		}

		return call;
	}

	private <V> V getHierarchical(Map<Class<?>, V> map, Class<?> cls) {
		Class<?> c = cls;
		while (c != null) {
			if (map.containsKey(c)) {
				return map.get(c);
			}
			c = c.getSuperclass();
		}
		return null;
	}

	@Override
	public HttpRequest generate(ActionPath<String> path) {
		StringBuilder sb = new StringBuilder();
		// add indentifier
		{
			Iterator<MethodInvocation<String>> it = path.getElements()
					.iterator();
			if (!it.hasNext()) {
				throw new RuntimeException(
						"Tried to generate URL of empty ActionPath");
			}

			MethodInvocation<String> element = it.next();
			sb.append(getHierarchical(controllerNameMap,
					element.getInstanceClass()));
		}

		// add methods
		for (MethodInvocation<String> element : path.getElements()) {
			sb.append(".");
			BiMap<Method, String> methodNameMap = getHierarchical(
					actionMethodNameMap, element.getInstanceClass());
			if (methodNameMap == null) {
				throw new RuntimeException(
						"Unable to find controller or embedded controller class "
								+ element.getInstanceClass());
			}
			sb.append(methodNameMap.get(element.getMethod()));
		}

		// add arguments
		for (MethodInvocation<String> element : path.getElements()) {
			for (String argument : element.getArguments()) {
				sb.append("/");
				sb.append(argument);
			}
		}
		return new HttpRequestImpl(sb.toString());
	}

	private Class<?> findControllerEntry(String servletPath) {
		// find the first dot in the path, which separates the controller from
		// the method
		int idx = servletPath.indexOf('.');
		if (idx < 0) {
			log.debug("No dot in servlet Path, cannot determine controller");
			return null;
		}

		// get the prefix
		String identifier = servletPath.substring(0, idx);

		return controllerNameMap.inverse().get(identifier);
	}

}
