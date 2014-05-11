package laf.requestProcessing;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.base.ActionResult;
import laf.configuration.ConfigInstance;
import laf.configuration.ConfigValue;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

public class RequestProcessingService {

	@ConfigValue("laf.requestProcessing.DefaultRequestProcessorFactory")
	@Inject
	ConfigInstance<RequestProcessorFactory> requestProcessorFactory;

	RequestProcessor processor;

	public ActionResult process(ActionPath<ParameterValueProvider> path) {
		return processor.process(path);
	}

	@PostConstruct
	void initialize() {
		processor = requestProcessorFactory.get().createProcessor();
	}

	public RequestProcessor getProcessor() {
		return processor;
	}
}
