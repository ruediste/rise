package com.github.ruediste.laf.mvc.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;

import com.github.ruediste.laf.core.CoreConfiguration;
import com.github.ruediste.laf.core.PathInfoIndex;
import com.github.ruediste.laf.core.front.reload.ClassHierarchyCache;
import com.github.ruediste.laf.core.httpRequest.HttpRequest;
import com.github.ruediste.laf.mvc.ActionPath;
import com.github.ruediste.laf.util.Pair;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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

	private HashMap<Pair<ClassNode, MethodNode>, String> methodToPathInfoMap = new HashMap<>();

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
		BiMap<Method, String> methodNameMap = HashBiMap.create();
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
							(pre, req) -> createActionPath(m));
				} else {
					// there are parameters or an embedded controller is
					// involved
					idx.registerPrefix(prefix, null);
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

				methodToPathInfoMap.put(controllerMethodPair,
						pathInfos.primaryPathInfo);
			}

		actionMethodNameMap.put(beanClass, methodNameMap);
	}

	public ActionPath<String> parse(String prefix, ClassNode classNode, MethodNode methodNode,
			HttpRequest request) {

		String remaining = request.getPathInfo().substring(prefix.length(),
				request.getPathInfo().length());
		
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(Type.getObjectType(classNode.name).getClassName());
		org.objectweb.asm.commons.Method m;
		Method m1;
		clazz.getMethod(remaining, null)

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

		return null;
	}

	@Override
	public HttpRequest generate(ActionPath<String> path) {
		// TODO Auto-generated method stub
		return null;
	}
}
