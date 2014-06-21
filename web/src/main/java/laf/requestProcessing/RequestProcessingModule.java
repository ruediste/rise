package laf.requestProcessing;

import javax.inject.Singleton;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.http.requestMapping.parameterValueProvider.ParameterValueProviderModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "request processing logic, independant of view technology", imported = { BaseModule.class, }, exported = {
		ParameterValueProviderModule.class, ActionPathModule.class })
public class RequestProcessingModule {

}
