package laf.component.reqestProcessing;

import javax.inject.Inject;

import laf.base.configuration.ConfigurationValue;
import laf.core.requestProcessing.RequestProcessor;

public class SwitchComponentRequestProcessor extends
		SwitchComponentRequestProcessorBase {

	@Inject
	ConfigurationValue<InitialRequestProcessorCP> initial;

	@Inject
	ConfigurationValue<ReloadRequestProcessorCP> reload;

	@Inject
	ConfigurationValue<ComponentActionRequestProcessorCP> componentAction;

	@Override
	protected RequestProcessor getReloadProcessor() {
		return reload.value().get();
	}

	@Override
	protected RequestProcessor getComponentActionProcessor() {
		return componentAction.value().get();
	}

	@Override
	protected RequestProcessor getInitialProcessor() {
		return initial.value().get();
	}

}
