package laf.mvc.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.core.http.request.HttpRequest;
import laf.core.http.request.HttpRequestImpl;
import laf.mvc.Controller;
import laf.mvc.actionPath.*;

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

	public void initialize(ControllerRepository controllerRepository,
			Function<Class<?>, String> nameMapper) {

		// initialize controller name map
		Controller controller = new Controller() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Controller.class;
			}
		};
		for (Bean<?> bean : beanManager.getBeans(Object.class, controller)) {
			Class<?> beanClass = bean.getBeanClass();
			controllerNameMap.put(beanClass, nameMapper.apply(beanClass));

			// build method name map
			BiMap<Method, String> methodNameMap = HashBiMap.create();
			for (Method m : beanClass.getMethods()) {
				String name = m.getName();

				// find unique name
				if (methodNameMap.inverse().containsKey(name)) {
					int i = 1;
					String tmp = name + "_" + i;
					while (methodNameMap.inverse().containsKey(tmp)) {
						i += 1;
					}
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

			ActionInvocation<String> invocation = new ActionInvocation<>(
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

	@Override
	public HttpRequest generate(ActionPath<String> path) {
		StringBuilder sb = new StringBuilder();
		// add indentifier
		{
			Iterator<ActionInvocation<String>> it = path.getElements()
					.iterator();
			if (!it.hasNext()) {
				throw new RuntimeException(
						"Tried to generate URL of empty ActionPath");
			}

			ActionInvocation<String> element = it.next();
			sb.append(controllerNameMap.get(element.getControllerClass()));
		}

		// add methods
		for (ActionInvocation<String> element : path.getElements()) {
			sb.append(".");
			sb.append(actionMethodNameMap.get(element.getControllerClass())
					.get(element.getMethod()));
		}

		// add arguments
		for (ActionInvocation<String> element : path.getElements()) {
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
