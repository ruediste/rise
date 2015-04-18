package com.github.ruediste.laf.core.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.ruediste.laf.util.AsmUtil;

public class ActionPathAnnotationUtil {

	public static class MethodPathInfos {
		public List<String> pathInfos = new ArrayList<>();
		public String primaryPathInfo;
	}

	public static MethodPathInfos getPathInfos(Method m,
			Supplier<String> defaultPathInfoSupplier) {
		MethodPathInfos result = new MethodPathInfos();
		String primaryPath = null;
		for (ActionPath path : m.getAnnotationsByType(ActionPath.class)) {
			result.pathInfos.add(path.value());
			if (path.primary()) {
				if (primaryPath != null) {
					throw new RuntimeException(
							"Multiple ActionPath annotations with primary=true found on method "
									+ m);
				}
				primaryPath = path.value();
			}
		}
		if (m.isAnnotationPresent(NoDefaultActionPath.class)) {
			if (primaryPath == null) {
				throw new RuntimeException(
						"No ActionPath marked as primaryPath, but NoDefaultActionPath annotation present");
			}
		} else {
			String defaultPathInfo = defaultPathInfoSupplier.get();
			result.pathInfos.add(defaultPathInfo);

			// there is no primary path yet, use the default path
			if (primaryPath == null) {
				primaryPath = defaultPathInfo;
			}
		}

		result.primaryPathInfo = primaryPath;
		return result;
	}

	public static MethodPathInfos getPathInfos(MethodNode m,
			Supplier<String> defaultPathInfoSupplier) {
		MethodPathInfos result = new MethodPathInfos();
		String primaryPath = null;

		if (m.visibleAnnotations != null)
			for (AnnotationNode path : AsmUtil.getAnnotationsByType(
					m.visibleAnnotations, ActionPath.class)) {
				AsmUtil.getString(path, "value");
				result.pathInfos.add(path.value());
				if (path.primary()) {
					if (primaryPath != null) {
						throw new RuntimeException(
								"Multiple ActionPath annotations with primary=true found on method "
										+ m);
					}
					primaryPath = path.value();
				}
			}
		if (m.isAnnotationPresent(NoDefaultActionPath.class)) {
			if (primaryPath == null) {
				throw new RuntimeException(
						"No ActionPath marked as primaryPath, but NoDefaultActionPath annotation present");
			}
		} else {
			String defaultPathInfo = defaultPathInfoSupplier.get();
			result.pathInfos.add(defaultPathInfo);

			// there is no primary path yet, use the default path
			if (primaryPath == null) {
				primaryPath = defaultPathInfo;
			}
		}

		result.primaryPathInfo = primaryPath;
		return result;
	}
}
