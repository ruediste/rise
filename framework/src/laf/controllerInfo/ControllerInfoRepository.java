package laf.controllerInfo;

import java.util.LinkedHashMap;

import javax.inject.Singleton;

import laf.controllerInfo.impl.ControllerInfoImpl;

@Singleton
public class ControllerInfoRepository {
	private final LinkedHashMap<String, ControllerInfo> controllerInfos = new LinkedHashMap<>();

	public void putControllerInfo(ControllerInfoImpl info) {
		controllerInfos.put(info.getQualifiedName(), info);
	}

	public Iterable<ControllerInfo> getControllerInfos() {
		return controllerInfos.values();
	}

	public ControllerInfo getControllerInfo(String qualifiedName) {
		return controllerInfos.get(qualifiedName);
	}
}
