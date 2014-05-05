package laf.requestProcessing.defaultProcessor;

import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathModule;
import laf.base.ActionResult;
import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProviderModule;
import laf.initialization.LafInitializer;
import laf.initialization.laf.LafConfigurationPhase;
import laf.initialization.laf.LafInitializationModule;
import laf.requestProcessing.RequestProcessingModule;
import laf.requestProcessing.RequestProcessingService;
import laf.requestProcessing.RequestProcessingService.ControllerInvokerImpl;
import laf.requestProcessing.RequestProcessingService.ParameterLoaderImpl;
import laf.requestProcessing.RequestProcessingService.RequestProcessor;

import org.jabsaw.Module;

@Module(description = "Default implementation of a Request Processor", imported = {
		RequestProcessingModule.class, LafInitializationModule.class,
		BaseModule.class, ControllerInfoModule.class,
		ParameterValueProviderModule.class, ActionPathModule.class })
public class DefaultRequestProcessorModule {

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

	private DefaultRequestProcessor defaultProcessor;

	public DefaultRequestProcessor getDefaultProcessor() {
		return defaultProcessor;
	}

	@Inject
	RequestProcessingService requestProcessingService;

	@LafInitializer(phase = LafConfigurationPhase.class)
	void initialize() {
		defaultProcessor = new DefaultRequestProcessor();
	}
}
