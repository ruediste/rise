package com.github.ruediste.laf.component.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.github.ruediste.laf.component.core.ActionInvocation;
import com.github.ruediste.laf.component.core.api.CController;
import com.github.ruediste.laf.core.base.*;
import com.github.ruediste.laf.core.http.request.HttpRequest;
import com.github.ruediste.laf.core.http.request.HttpRequestImpl;
import com.github.ruediste.laf.core.web.annotation.ActionPathAnnotationUtil;
import com.github.ruediste.laf.core.web.annotation.ActionPathAnnotationUtil.MethodPathInfos;
import com.google.common.base.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.net.UrlEscapers;

public class RequestMapperImpl implements RequestMapper {
	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	private HashMap<String, Pair<Class<?>, Method>> pathInfoMap = new HashMap<>();
	private PrefixMap<Pair<Class<?>, Method>> pathInfoPrefixMap = new PrefixMap<>();
	private HashMap<Pair<Class<?>, Method>, String> methodToPathInfoMap = new HashMap<>();

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

			// build method name map
			BiMap<Method, String> methodNameMap = HashBiMap.create();
			for (Method m : beanClass.getMethods()) {
				if (!ActionResult.class.isAssignableFrom(m.getReturnType())) {
					continue;
				}
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
				actionMethodNameMap.put(beanClass, methodNameMap);

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
				log.info("found method " + name);
			}

		}

	}

	@Override
	public ActionInvocation<String> parse(HttpRequest request) {
		String pathInfo = request.getPathInfo();
		if (Strings.isNullOrEmpty(pathInfo)) {
			pathInfo = "/";
		}
		Pair<Class<?>, Method> pair = pathInfoMap.get(pathInfo);
		String prefix = null;
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
			log.debug("Component: unable to parse servlet path " + pathInfo);
			return null;
		}

		Class<?> controllerClass = pair.getA();
		Method method = pair.getB();

		MethodInvocation<String> invocation = new MethodInvocation<>(
				controllerClass, method);

		List<String> parts = Splitter.on('/').omitEmptyStrings()
				.splitToList(pathInfo.substring(prefix.length()));
		if (parts.size() != method.getParameterCount()) {
			throw new RuntimeException(
					"Argument count of method invocation does not match. Path: "
							+ pathInfo + "; parsed arguments: " + parts
							+ "; method: " + method);
		}
		invocation.getArguments().addAll(parts);

		return new ActionInvocation<>(invocation);
	}

	@Override
	public HttpRequest generate(ActionInvocation<String> actionInvocation) {
		MethodInvocation<String> invocation = actionInvocation.getInvocation();
		StringBuilder sb = new StringBuilder();

		sb.append(methodToPathInfoMap.get(Pair.of(
				invocation.getInstanceClass(), invocation.getMethod())));

		// add arguments
		for (String argument : invocation.getArguments()) {
			sb.append("/");
			sb.append(UrlEscapers.urlFragmentEscaper().escape(argument));
		}
		return new HttpRequestImpl(sb.toString());
	}

}
