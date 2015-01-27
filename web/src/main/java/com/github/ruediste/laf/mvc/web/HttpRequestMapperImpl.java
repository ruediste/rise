package com.github.ruediste.laf.mvc.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.core.base.*;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.http.request.HttpRequestImpl;
import com.github.ruediste.laf.core.web.annotation.ActionPathAnnotationUtil;
import com.github.ruediste.laf.core.web.annotation.ActionPathAnnotationUtil.MethodPathInfos;
import com.github.ruediste.laf.mvc.core.ActionPath;
import com.github.ruediste.laf.mvc.core.ControllerReflectionUtil;
import com.github.ruediste.laf.mvc.core.api.MController;
import com.google.common.base.*;
import com.google.common.collect.*;

public class HttpRequestMapperImpl implements HttpRequestMapper {
	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	private HashMap<String, Pair<Class<?>, Method>> pathInfoMap = new HashMap<>();
	private PrefixMap<Pair<Class<?>, Method>> pathInfoPrefixMap = new PrefixMap<>();
	private HashMap<Pair<Class<?>, Method>, String> methodToPathInfoMap = new HashMap<>();

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

				// add the path infos for the method to the respective maps
				Pair<Class<?>, Method> controllerMethodPair = Pair.of(
						beanClass, m);
				MethodPathInfos pathInfos = ActionPathAnnotationUtil
						.getPathInfos(m,
								() -> "/" + controllerName + "." + m.getName());
				for (String path : pathInfos.pathInfos) {
					if (m.getParameterCount() == 0) {
						pathInfoMap.put(path, controllerMethodPair);
					} else {
						pathInfoPrefixMap.put(path, controllerMethodPair);
					}
				}

				methodToPathInfoMap.put(controllerMethodPair,
						pathInfos.primaryPathInfo);
			}

			actionMethodNameMap.put(beanClass, methodNameMap);
		}

	}

	@Override
	public ActionPath<String> parse(HttpRequest request) {
		String pathInfo = request.getPathInfo();
		if (Strings.isNullOrEmpty(pathInfo)) {
			pathInfo = "/";
		}

		ActionPath<String> call = new ActionPath<>();
		String prefix = null;
		Pair<Class<?>, Method> pair = pathInfoMap.get(pathInfo);
		if (pair != null) {
			prefix = pathInfo;
		} else {
			Entry<String, Pair<Class<?>, Method>> entry = pathInfoPrefixMap
					.getEntry(pathInfo);
			if (entry != null) {
				prefix = entry.getKey();
				pair = entry.getValue();
			}
		}

		if (pair == null) {
			return null;
		}

		Class<?> controllerClass = pair.getA();

		// remove the identifier and split the suffix into parts at the /
		// characters
		List<String> parts = Splitter.on('/').splitToList(
				pathInfo.substring(prefix.length()));

		// determine action methods
		ArrayList<Method> actionMethods = new ArrayList<>();
		actionMethods.add(pair.getB());
		if (!parts.isEmpty()) {
			for (String actionName : parts.get(0).split("\\.")) {
				if (Strings.isNullOrEmpty(actionName)) {
					continue;
				}
				Method actionMethod = actionMethodNameMap.get(controllerClass)
						.inverse().get(actionName);

				if (actionMethod == null) {
					log.debug("no ActionMethod named " + actionName + " found");
					return null;
				}
				actionMethods.add(actionMethod);

				if (ControllerReflectionUtil.isEmbeddedController(actionMethod
						.getReturnType())) {
					// update the controller class to the embedded controller
					controllerClass = actionMethod.getReturnType();
				}
			}
		}

		// collect arguments
		controllerClass = pair.getA();
		int i = 1;
		for (Method actionMethod : actionMethods) {

			MethodInvocation<String> invocation = new MethodInvocation<>(
					controllerClass, actionMethod);

			for (; i < parts.size(); i++) {
				invocation.getArguments().add(parts.get(i));
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

			String pathInfo = methodToPathInfoMap.get(Pair.of(
					element.getInstanceClass(), element.getMethod()));
			sb.append(pathInfo);
		}

		// add methods
		for (MethodInvocation<String> element : Iterables.skip(
				path.getElements(), 1)) {
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

}
