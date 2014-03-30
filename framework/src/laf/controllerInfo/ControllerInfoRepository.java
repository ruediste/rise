package laf.controllerInfo;

import java.util.LinkedHashMap;

import javax.inject.Singleton;

import laf.controllerInfo.impl.ControllerInfoImpl;

@Singleton
public class ControllerInfoRepository {
	private final LinkedHashMap<Class<?>, ControllerInfo> controllerInfos = new LinkedHashMap<>();

	public void putControllerInfo(ControllerInfoImpl info) {
		controllerInfos.put(info.getControllerClass(), info);
	}

	public Iterable<ControllerInfo> getControllerInfos() {
		return controllerInfos.values();
	}

	public ControllerInfo getControllerInfo(Class<?> controllerClass) {
		return controllerInfos.get(controllerClass);
	}
}
