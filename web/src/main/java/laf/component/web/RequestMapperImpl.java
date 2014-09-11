package laf.component.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.component.core.ActionInvocation;
import laf.component.core.api.CController;
import laf.core.base.MethodInvocation;
import laf.core.http.request.HttpRequest;
import laf.core.http.request.HttpRequestImpl;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class RequestMapperImpl implements RequestMapper {
	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	private BiMap<Class<?>, String> controllerNameMap = HashBiMap.create();

	private HashMap<Class<?>, BiMap<Method, String>> actionMethodNameMap = new HashMap<>();

	public void initialize(Function<Class<?>, String> nameMapper) {

		// initialize controller name map
		CController controller = new CController() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return CController.class;
			}
		};
		for (Bean<?> bean : beanManager.getBeans(Object.class, controller)) {
			Class<?> beanClass = bean.getBeanClass();
			String controllerName = nameMapper.apply(beanClass);
			log.info("Found Controller " + beanClass.getName() + " -> "
					+ controllerName);
			controllerNameMap.put(beanClass, controllerName);

			// build method name map
			BiMap<Method, String> methodNameMap = HashBiMap.create();
			for (Method m : beanClass.getMethods()) {
				String name = m.getName();

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
				log.info("found method " + name);
			}

			actionMethodNameMap.put(beanClass, methodNameMap);
		}

	}

	@Override
	public ActionInvocation<String> parse(HttpRequest request) {

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

		String actionMethodName = parts[0].substring(1);
		Method method = actionMethodNameMap.get(controllerClass).inverse()
				.get(actionMethodName);
		MethodInvocation<String> invocation = new MethodInvocation<>(
				controllerClass, method);

		for (int i = 1; i < parts.length; i++) {
			invocation.getArguments().add(parts[i]);
		}

		return new ActionInvocation<>(invocation);
	}

	@Override
	public HttpRequest generate(ActionInvocation<String> actionInvocation) {
		MethodInvocation<String> invocation = actionInvocation.getInvocation();
		StringBuilder sb = new StringBuilder();
		// add indentifier
		sb.append(controllerNameMap.get(invocation.getInstanceClass()));

		// add method
		sb.append(".");
		BiMap<Method, String> methodNameMap = actionMethodNameMap
				.get(invocation.getInstanceClass());
		if (methodNameMap == null) {
			throw new RuntimeException("The class"
					+ invocation.getInstanceClass().getName()
					+ " is not registered as component controller");
		}
		sb.append(methodNameMap.get(invocation.getMethod()));

		// add arguments
		for (String argument : invocation.getArguments()) {
			sb.append("/");
			sb.append(argument);
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
