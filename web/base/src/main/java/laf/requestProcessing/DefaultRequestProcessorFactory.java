package laf.requestProcessing;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class DefaultRequestProcessorFactory implements RequestProcessorFactory {

	@Inject
	Instance<DefaultRequestProcessor> processor;

	@Override
	public RequestProcessor createProcessor() {
		return processor.get();
	}

}
