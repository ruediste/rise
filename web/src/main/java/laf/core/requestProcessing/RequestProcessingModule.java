package laf.core.requestProcessing;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.core.actionPath.ActionPathModule;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProviderModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "request processing logic, independant of view technology", imported = { BaseModule.class, }, exported = {
		ParameterValueProviderModule.class, ActionPathModule.class })
public class RequestProcessingModule {

}
