package laf.core.web.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import laf.component.web.ActionPath;
import laf.component.web.NoDefaultActionPath;

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
							"Multiple ActionPath annotations with mainPath=true found on method "
									+ m);
				}
				primaryPath = path.value();
			}
		}
		if (m.isAnnotationPresent(NoDefaultActionPath.class)) {
			if (primaryPath == null) {
				throw new RuntimeException(
						"No ActionPath marked as mainPath, but NoDefaultActionPath annotation present");
			}
		} else {
			String defaultPathInfo = defaultPathInfoSupplier.get();
			result.pathInfos.add(defaultPathInfo);

			// there is no mainPath yet, use the default path
			if (primaryPath == null) {
				primaryPath = defaultPathInfo;
			}
		}

		result.primaryPathInfo = primaryPath;
		return result;
	}
}
