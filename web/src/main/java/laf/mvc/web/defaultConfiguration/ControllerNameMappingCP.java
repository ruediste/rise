package laf.mvc.web.defaultConfiguration;

import laf.base.configuration.ConfigurationParameter;

import com.google.common.base.Function;

public interface ControllerNameMappingCP extends
		ConfigurationParameter<Function<Class<?>, String>> {
}