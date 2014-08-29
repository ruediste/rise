package laf.core.defaultConfiguration;

import laf.core.base.configuration.ConfigurationParameter;

import com.google.common.base.Function;

public interface ControllerNameMappingCP extends
		ConfigurationParameter<Function<Class<?>, String>> {
}