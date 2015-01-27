package com.github.ruediste.laf.core.defaultConfiguration;

import com.github.ruediste.laf.core.base.configuration.ConfigurationParameter;
import com.google.common.base.Function;

public interface ControllerNameMappingCP extends
		ConfigurationParameter<Function<Class<?>, String>> {
}