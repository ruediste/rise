package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import javax.inject.Inject;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.RequestParseResult;
import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.core.httpRequest.HttpRequestImpl;
import com.github.ruediste.laf.mvc.ActionInvocation;
import com.github.ruediste.laf.util.AsmUtil;
import com.github.ruediste.laf.util.AsmUtil.MethodRef;
import com.github.ruediste.laf.util.MethodInvocation;
import com.github.ruediste.laf.util.Pair;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

public class MvcWebRequestMapperImpl implements MvcWebRequestMapper {

	@Inject
	Logger log;

	@Inject
	CoreConfiguration coreConfig;

	@Inject
	ClassHierarchyCache cache;

	@Inject
	PathInfoIndex idx;

	@Inject
	MvcWebControllerReflectionUtil util;

	/**
	 * Map controller methods to their prefixes. Prefixes do not include a final
	 * "." or "/". Used for {@link #generate(ActionInvocation)}.
	 */
	private HashMap<Pair<Class<?>, Method>, String> methodToPrefixMap = new HashMap<>();

	/**
	 * Map between methods and their action method names, grouped by class
	 */
	private HashMap<String, BiMap<MethodRef, String>> actionMethodNameMap = new HashMap<>();

	public void registerControllers() {
		String internalName = Type.getInternalName(IControllerMvcWeb.class);
		registerControllers(internalName);
	}

	private void registerControllers(String internalName) {
		for (ClassNode child : cache.getChildren(internalName)) {
			register(child);
			registerControllers(child.name);
		}
	}

	private void register(ClassNode cls) {
		String controllerName = coreConfig.calculateControllerName(cls);
		log.debug("found controller " + cls.name + " -> " + controllerName);

		// build method name map
		BiMap<MethodNode, String> methodNameMap = HashBiMap.create();
		if (cls.methods != null)
			for (MethodNode m : cls.methods) {
				if (!util.isActionMethod(m)) {
					continue;
				}
				String name = m.name;
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

				MethodRef methodRef = new MethodRef(cls.name, m.name, m.desc);
				String prefix = controllerName + "." + name;

				// add the path infos for the method to the respective maps
				if ((m.parameters == null || m.parameters.size() == 0)) {
					// no parameters
					idx.registerPathInfo(
							prefix,
							(pre, req) -> result(createInvocation(cls,
									methodRef)));
				} else {
					// there are parameters or an embedded controller is
					// involved
					idx.registerPrefix(
							prefix,
							(pre, req) -> result(parse(prefix, cls, methodRef,
									req)));
				}
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

				methodToPrefixMap.put(controllerMethodPair,
						pathInfos.primaryPathInfo);
			}

		actionMethodNameMap.put(beanClass, methodNameMap);
	}

	public RequestParseResult result(ActionInvocation<String> path) {
		return null;
	}

	/**
	 * Parse a request. The prefix must include the method name and the first
	 * "/". The remaining pathInfo has the form
	 * 
	 * <pre>
	 * ({argument}("/"{argument})*)?
	 * </pre>
	 */
	public ActionInvocation<String> parse(String prefix,
			ClassNode controllerClassNode, MethodRef methodRef,
			HttpRequest request) throws Exception {
		try {

			ActionInvocation<String> invocation = createInvocation(
					controllerClassNode, methodRef);

			String remaining = request.getPathInfo().substring(prefix.length(),
					request.getPathInfo().length());

			// collect arguments
			invocation.methodInvocation.getArguments().addAll(
					Splitter.on('/').splitToList(remaining));
			return invocation;
		} catch (Exception e) {
			throw new RuntimeException("Error while loading "
					+ controllerClassNode.name + "." + methodRef.methodName
					+ "(" + methodRef.desc + ")", e);
		}

	}

	/**
	 * Create an {@link ActionInvocation} without parameters
	 */
	protected ActionInvocation<String> createInvocation(
			ClassNode controllerClassNode, MethodRef methodRef)
			throws ClassNotFoundException, ReflectiveOperationException {
		ActionInvocation<String> invocation = new ActionInvocation<>();

		// load method
		Class<?> controllerClass = AsmUtil.loadClass(
				Type.getObjectType(controllerClassNode.name),
				coreConfig.dynamicClassLoader);
		Method method = AsmUtil.loadMethod(methodRef,
				coreConfig.dynamicClassLoader);
		invocation.methodInvocation = new MethodInvocation<>(controllerClass,
				method);
		return invocation;
	}

	@Override
	public HttpRequest generate(ActionInvocation<String> path) {
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

			String prefix = methodToPrefixMap.get(Pair.of(
					element.getInstanceClass(), element.getMethod()));
			sb.append(prefix);
		}

		// add methods
		for (MethodInvocation<String> element : Iterables.skip(
				path.getElements(), 1)) {
			sb.append(".");
			sb.append(getEmbeddedActionMethodName(element));
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
