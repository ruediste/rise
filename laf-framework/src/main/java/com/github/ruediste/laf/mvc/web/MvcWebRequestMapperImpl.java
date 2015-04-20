package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.util.HashMap;

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
import com.github.ruediste.laf.core.web.ActionPathAnnotationUtil;
import com.github.ruediste.laf.core.web.ActionPathAnnotationUtil.MethodPathInfos;
import com.github.ruediste.laf.mvc.ActionInvocation;
import com.github.ruediste.laf.mvc.MvcControllerReflectionUtil;
import com.github.ruediste.laf.util.AsmUtil;
import com.github.ruediste.laf.util.AsmUtil.MethodRef;
import com.github.ruediste.laf.util.MethodInvocation;
import com.github.ruediste.laf.util.Pair;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MvcWebRequestMapperImpl implements MvcWebRequestMapper {

	@Inject
	Logger log;

	@Inject
	CoreConfiguration coreConfig;

	@Inject
	MvcWebConfiguration mvcWebConfig;

	@Inject
	ClassHierarchyCache cache;

	@Inject
	PathInfoIndex idx;

	@Inject
	MvcControllerReflectionUtil util;

	/**
	 * Map controller methods to their prefixes. Prefixes do not include a final
	 * "." or "/". Used for {@link #generate(ActionInvocation)}.
	 */
	private HashMap<Pair<String, MethodRef>, String> methodToPrefixMap = new HashMap<>();

	/**
	 * Map between methods and their action method names, grouped by class
	 */
	private HashMap<String, BiMap<MethodRef, String>> actionMethodNameMap = new HashMap<>();

	@Override
	public void registerControllers() {
		String internalName = Type.getInternalName(IControllerMvcWeb.class);
		registerControllers(internalName);
	}

	void registerControllers(String internalName) {
		for (ClassNode child : cache.getChildren(internalName)) {
			register(child);
			registerControllers(child.name);
		}
	}

	void register(ClassNode cls) {
		String controllerName = coreConfig.calculateControllerName(cls);
		log.debug("found controller " + cls.name + " -> " + controllerName);

		// build method name map
		BiMap<MethodRef, String> methodNameMap = HashBiMap.create();
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

				MethodRef methodRef = new MethodRef(cls.name, m.name, m.desc);
				methodNameMap.put(methodRef, name);

				// determine the path infos to register under
				MethodPathInfos pathInfos = ActionPathAnnotationUtil
						.getPathInfos(m, () -> "/" + controllerName + "."
								+ m.name);

				// add the path infos for the method to the respective maps
				if (Type.getArgumentTypes(m.desc).length == 0) {
					// no parameters
					for (String prefix : pathInfos.pathInfos) {
						idx.registerPathInfo(prefix,
								req -> result(createInvocation(cls, methodRef)));
					}
				} else {
					// there are parameters
					for (String prefix : pathInfos.pathInfos) {
						String s = prefix + "/";

						idx.registerPrefix(s,
								req -> result(parse(s, cls, methodRef, req)));
					}
				}

				methodToPrefixMap.put(Pair.of(cls.name, methodRef),
						pathInfos.primaryPathInfo);
			}

		actionMethodNameMap.put(cls.name, methodNameMap);
	}

	public RequestParseResult result(ActionInvocation<String> path) {
		return new MvcWebRequestParseResult(mvcWebConfig, path);
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
			HttpRequest request) {
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
			ClassNode controllerClassNode, MethodRef methodRef) {
		ActionInvocation<String> invocation = new ActionInvocation<>();

		// load method
		try {
			Class<?> controllerClass;
			controllerClass = AsmUtil.loadClass(
					Type.getObjectType(controllerClassNode.name),
					coreConfig.dynamicClassLoader);
			Method method = AsmUtil.loadMethod(methodRef,
					coreConfig.dynamicClassLoader);
			invocation.methodInvocation = new MethodInvocation<>(
					controllerClass, method);
		} catch (Exception e) {
			throw new RuntimeException("Error while creating invocation for "
					+ controllerClassNode.name + "." + methodRef.methodName, e);
		}
		return invocation;
	}

	@Override
	public HttpRequest generate(ActionInvocation<String> path) {
		StringBuilder sb = new StringBuilder();
		MethodRef ref = MethodRef.of(path.methodInvocation.getMethod());
		String prefix = methodToPrefixMap.get(Pair.of(
				Type.getInternalName(path.controllerClass), ref));
		if (prefix == null)
			throw new RuntimeException("Unable to find prefix for " + ref);
		sb.append(prefix);

		// add arguments
		for (String argument : path.methodInvocation.getArguments()) {
			sb.append("/");
			sb.append(argument);
		}
		return new HttpRequestImpl(sb.toString());

	}
}
