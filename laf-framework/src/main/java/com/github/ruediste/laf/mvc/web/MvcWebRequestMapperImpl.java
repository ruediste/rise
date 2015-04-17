package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
import com.github.ruediste.laf.mvc.ActionPath;
import com.github.ruediste.laf.util.AsmUtil;
import com.github.ruediste.laf.util.AsmUtil.MethodRef;
import com.github.ruediste.laf.util.MethodInvocation;
import com.github.ruediste.laf.util.Pair;
import com.github.ruediste.salta.standard.util.MethodOverrideIndex;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

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
	 * "." or "/". Used for {@link #generate(ActionPath)}.
	 */
	private HashMap<Pair<Class<?>, Method>, String> methodToPrefixMap = new HashMap<>();

	/**
	 * Map between methods and their action method names, grouped by class
	 */
	private HashMap<String, BiMap<MethodRef, String>> actionMethodNameMap = new HashMap<>();

	/**
	 * Map between embedded controller names and their action method names.
	 */
	private ConcurrentHashMap<Class<?>, BiMap<Method, String>> embeddedActionMethodNameMap = new ConcurrentHashMap<>();

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

				String prefix = controllerName + "." + name;

				// add the path infos for the method to the respective maps
				if ((m.parameters == null || m.parameters.size() == 0)
						&& !util.isEmbeddedController(Type
								.getReturnType(m.desc))) {
					// no parameters
					idx.registerPathInfo(prefix,
							(pre, req) -> result(createActionPath(cls, m)));
				} else {
					// there are parameters or an embedded controller is
					// involved
					idx.registerPrefix(
							prefix,
							(pre, req) -> result(parse(prefix, cls, null, null)));
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

	public RequestParseResult result(ActionPath<String> path) {
		return null;
	}

	/**
	 * Create an {@link ActionPath} for a single method without parameters
	 */
	public ActionPath<String> createActionPath(ClassNode controllerClassNode,
			MethodNode methodNode) {
		ActionPath<String> result = new ActionPath<>();
		try {
			MethodInvocation<String> i = new MethodInvocation<>(
					AsmUtil.loadClass(
							Type.getObjectType(controllerClassNode.name),
							coreConfig.dynamicClassLoader), AsmUtil.loadMethod(
							new MethodRef(controllerClassNode.name,
									methodNode.name, methodNode.desc),
							coreConfig.dynamicClassLoader));
			result.getElements().add(i);
		} catch (Exception e) {
			throw new RuntimeException("Error while loading "
					+ controllerClassNode.name + "." + methodNode.name + "("
					+ methodNode.desc + ")", e);
		}
		return result;
	}

	/**
	 * Parse a request. The prefix must include the method name. The remaining
	 * pathInfo has the form
	 * 
	 * <pre>
	 * ({methodName]("."{methodName})*)?("/"{argument})*
	 * </pre>
	 * 
	 * Thus, if an embedded controller is called, the prefix needs to include a
	 * final "."
	 */
	public ActionPath<String> parse(String prefix,
			ClassNode controllerClassNode, MethodRef methodRef,
			HttpRequest request) throws Exception {

		Class<?> initialControllerClass = AsmUtil.loadClass(
				Type.getObjectType(controllerClassNode.name),
				coreConfig.dynamicClassLoader);
		Method method = AsmUtil.loadMethod(methodRef,
				coreConfig.dynamicClassLoader);

		String remaining = request.getPathInfo().substring(prefix.length(),
				request.getPathInfo().length());

		List<String> parts = Splitter.on('/').splitToList(remaining);

		TypeToken<?> embeddedControllerType = TypeToken.of(
				initialControllerClass).resolveType(
				method.getGenericReturnType());

		// determine action methods
		ArrayList<MethodInvocation<String>> invocations = new ArrayList<>();
		invocations.add(new MethodInvocation<>(initialControllerClass, method));
		if (!parts.isEmpty()) {
			for (String actionName : Splitter.on('.').split(parts.get(0))) {
				if (Strings.isNullOrEmpty(actionName)) {
					continue;
				}

				Class<?> embeddedControllerClass = embeddedControllerType
						.getRawType();
				method = getEmbeddedActionMethod(embeddedControllerClass,
						actionName);

				if (method == null) {
					log.debug("no ActionMethod named " + actionName + " found");
					return null;
				}

				invocations.add(new MethodInvocation<>(embeddedControllerClass,
						method));

				TypeToken<?> returnType = embeddedControllerType
						.resolveType(method.getGenericReturnType());
				if (util.isEmbeddedController(returnType)) {
					// update the controller class to the embedded controller
					embeddedControllerType = returnType;
				} else {
					embeddedControllerType = null;
				}
			}
		}

		ActionPath<String> call = new ActionPath<>();

		// collect arguments
		int i = 1;
		for (MethodInvocation<String> invocation : invocations) {

			for (; i < parts.size(); i++) {
				invocation.getArguments().add(parts.get(i));
			}
			call.getElements().add(invocation);
		}

		return call;
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

	protected Method getEmbeddedActionMethod(Class<?> embeddedControllerClass,
			String actionName) {
		return getEmbeddedActionMethodNameMap(embeddedControllerClass)
				.inverse().get(actionName);
	}

	protected BiMap<Method, String> getEmbeddedActionMethodNameMap(
			Class<?> embeddedControllerClass) {
		if (!util.isEmbeddedController(TypeToken.of(embeddedControllerClass)))
			throw new RuntimeException(embeddedControllerClass
					+ " is not an embedded controller class");
		return embeddedActionMethodNameMap
				.computeIfAbsent(embeddedControllerClass,
						x -> {
							BiMap<Method, String> result = HashBiMap.create();
							MethodOverrideIndex idx = new MethodOverrideIndex(
									embeddedControllerClass);
							Class<?> cls = embeddedControllerClass;
							while (cls != null) {
								for (Method m : cls.getDeclaredMethods()) {
									if (!util.isActionMethod(m))
										continue;
									if (idx.isOverridden(m))
										continue;
									String name = m.getName();
									for (int i = 0; result.inverse()
											.containsKey(name); i++) {
										name = m.getName() + "_" + i;
									}
									result.put(m, name);
								}
								cls = cls.getSuperclass();
							}
							return result;
						});
	}

	protected String getEmbeddedActionMethodName(
			MethodInvocation<String> element) {
		return getEmbeddedActionMethodNameMap(element.getInstanceClass()).get(
				element.getMethod());
	}
}
