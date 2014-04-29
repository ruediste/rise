package laf.controllerInfo;

import java.util.LinkedHashMap;

import javax.inject.Singleton;

@Singleton
public class ControllerInfoRepository {
	private final LinkedHashMap<Class<?>, ControllerInfo> controllerInfos = new LinkedHashMap<>();

	public void putControllerInfo(ControllerInfo info) {
		controllerInfos.put(info.getControllerClass(), info);
	}

	public Iterable<ControllerInfo> getControllerInfos() {
		return controllerInfos.values();
	}

	public ControllerInfo getControllerInfo(Class<?> controllerClass) {
		return controllerInfos.get(controllerClass);
	}

}
