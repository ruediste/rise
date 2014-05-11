package laf.requestProcessing;

import javax.inject.Singleton;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProviderModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "request processing logic, independant of view technology", imported = {
		BaseModule.class, ControllerInfoModule.class, ActionPathModule.class }, exported = { ParameterValueProviderModule.class })
public class RequestProcessingModule {

	private RequestProcessor processor;

	public RequestProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(RequestProcessor processor) {
		this.processor = processor;
	}

}
