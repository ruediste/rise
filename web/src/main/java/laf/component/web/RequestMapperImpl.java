package laf.component.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.component.core.ActionInvocation;
import laf.component.core.api.CController;
import laf.core.base.*;
import laf.core.http.request.HttpRequest;
import laf.core.http.request.HttpRequestImpl;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class RequestMapperImpl implements RequestMapper {
	@Inject
	Logger log;

	@Inject
	BeanManager beanManager;

	private HashMap<String, Pair<Class<?>, Method>> directActionPath = new HashMap<>();
	private PrefixMap<Pair<Class<?>, Method>> actionPathMap = new PrefixMap<>();
	private HashMap<Pair<Class<?>, Method>, String> methodPrefixMap = new HashMap<>();

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

				String mainPath = null;
				for (ActionPath path : m.getAnnotationsByType(ActionPath.class)) {
					if (m.getParameterCount() == 0) {
						directActionPath.put(path.value(),
								Pair.of(beanClass, m));
					} else {
						actionPathMap.put(path.value(), Pair.of(beanClass, m));
					}
					if (path.mainPath()) {
						if (mainPath != null) {
							throw new RuntimeException(
									"Multiple ActionPath annotations with mainPath=true found on method "
											+ m);
						}
						mainPath = path.value();
					}
				}
				if (m.isAnnotationPresent(NoDefaultActionPath.class)) {
					if (mainPath == null) {
						throw new RuntimeException(
								"No ActionPath marked as mainPath, but NoDefaultActionPath annotation present");
					}
				} else {
					if (m.getParameterCount() == 0) {
						directActionPath.put(controllerName + "." + name,
								Pair.of(beanClass, m));
					} else {
						actionPathMap.put(controllerName + "." + name,
								Pair.of(beanClass, m));
					}

					// there is no mainPath yet, use the default path
					if (mainPath == null) {
						mainPath = controllerName + "." + name;
					}
				}

				methodPrefixMap.put(Pair.of(beanClass, m), mainPath);
				log.info("found method " + name);
			}

			actionMethodNameMap.put(beanClass, methodNameMap);
		}

	}

	@Override
	public ActionInvocation<String> parse(HttpRequest request) {
		Pair<Class<?>, Method> pair = directActionPath.get(request.getPath());
		String prefix = null;
		if (pair != null) {
			prefix = request.getPath();
		} else {
			Entry<String, Pair<Class<?>, Method>> entry = actionPathMap
					.getEntry(request.getPath());
			if (entry != null) {
				prefix = entry.getKey();
				pair = entry.getValue();
			}
		}
		if (pair == null) {
			log.debug("Component: unable to parse servlet path " + request);
			return null;
		}

		Class<?> controllerClass = pair.getA();
		Method method = pair.getB();

		MethodInvocation<String> invocation = new MethodInvocation<>(
				controllerClass, method);

		List<String> parts = Splitter.on('/').omitEmptyStrings()
				.splitToList(request.getPath().substring(prefix.length()));
		if (parts.size() != method.getParameterCount()) {
			throw new RuntimeException(
					"Argument count of method invocation does not match. Path: "
							+ request.getPath() + "; parsed arguments: "
							+ parts + "; method: " + method);
		}
		invocation.getArguments().addAll(parts);

		return new ActionInvocation<>(invocation);
	}

	@Override
	public HttpRequest generate(ActionInvocation<String> actionInvocation) {
		MethodInvocation<String> invocation = actionInvocation.getInvocation();
		StringBuilder sb = new StringBuilder();

		sb.append(methodPrefixMap.get(Pair.of(invocation.getInstanceClass(),
				invocation.getMethod())));

		// add arguments
		for (String argument : invocation.getArguments()) {
			sb.append("/");
			sb.append(argument);
		}
		return new HttpRequestImpl(sb.toString());
	}

}
