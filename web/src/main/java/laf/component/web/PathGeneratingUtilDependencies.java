package laf.component.web;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import laf.core.http.HttpService;

public class PathGeneratingUtilDependencies {
	@Inject
	Instance<ActionInvocationBuilder> builderInstance;

	@Inject
	RequestMappingUtil requestMappingUtil;

	@Inject
	HttpService service;
}
