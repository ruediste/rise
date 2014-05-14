package laf.controllerInfo;

import java.util.LinkedHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ControllerInfoRepository {
	@Inject
	ControllerInfoRepositoryInitializer initializer;

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

	@PostConstruct
	void initialize() {
		initializer.initialize(this);
	}
}
