package laf.requestProcessing.defaultProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathModule;
import laf.base.ActionResult;
import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProviderModule;
import laf.requestProcessing.*;
import laf.requestProcessing.RequestProcessingService.ControllerInvokerImpl;
import laf.requestProcessing.RequestProcessingService.ParameterLoaderImpl;

import org.jabsaw.Module;

@Singleton
@Module(description = "Default implementation of a Request Processor", imported = {
		RequestProcessingModule.class, BaseModule.class,
		ControllerInfoModule.class, ParameterValueProviderModule.class,
		ActionPathModule.class })
public class DefaultRequestProcessorModule {

	@Inject
	RequestProcessingService requestProcessingService;

	@Inject
	RequestProcessingModule requestProcessingModule;

	public final class DefaultRequestProcessor implements RequestProcessor {
		private ParameterLoaderImpl loader;
		private ControllerInvokerImpl invoker;

		public DefaultRequestProcessor() {
			loader = requestProcessingService.new ParameterLoaderImpl();
			invoker = requestProcessingService.new ControllerInvokerImpl();
		}

		@Override
		public ActionResult process(ActionPath<ParameterValueProvider> path) {
			return invoker.invoke(loader.load(path));
		}
	}

	public void initialize() {
		requestProcessingModule.setProcessor(new DefaultRequestProcessor());
	}
}
