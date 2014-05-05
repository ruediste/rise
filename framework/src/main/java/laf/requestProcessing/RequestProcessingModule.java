package laf.requestProcessing;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProviderModule;

import org.jabsaw.Module;

@Module(description = "request processing logic, independant of view technology", imported = {
		BaseModule.class, ControllerInfoModule.class, ActionPathModule.class }, exported = { ParameterValueProviderModule.class })
public class RequestProcessingModule {

}
