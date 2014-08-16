package sampleApp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.base.configuration.*;
import laf.component.defaultConfiguration.DefaultComponentConfiguration;
import laf.core.defaultConfiguration.DefaultConfiguration;
import laf.core.http.request.HttpRequest;
import laf.core.http.requestMapping.HttpRequestMappingRule;
import laf.core.http.requestMapping.HttpRequestMappingRules;
import laf.core.http.requestMapping.parameterValueProvider.ParameterValueProvider;
import laf.mvc.actionPath.ActionPath;
import laf.mvc.actionPath.ActionPathFactory;
import laf.mvc.configuration.MvcDefaultConfiguration;

@ApplicationScoped
public class SampleAppConfiguration implements ConfigurationDefiner {

	@Inject
	DefaultConfiguration defaultConfiguration;
	@Inject
	MvcDefaultConfiguration mvcDefaultConfiguration;

	@Inject
	DefaultComponentConfiguration defaultComponentConfiguration;

	protected void registerConfigurationValueProviders(
			@Observes DiscoverConfigruationEvent e) {
		e.add(defaultConfiguration);
		e.add(mvcDefaultConfiguration);
		e.add(defaultComponentConfiguration);
		e.add(this);
		e.addPropretiesFile("configuration.properties");
		e.lock();
	}

	@Inject
	ActionPathFactory pathFactory;

	@ExtendConfiguration
	public void produce(HttpRequestMappingRules rules) {
		rules.get().add(new HttpRequestMappingRule() {

			@SuppressWarnings("unchecked")
			@Override
			public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
				return (ActionPath<ParameterValueProvider>) pathFactory
						.buildActionPath(null)
						.controller(SampleController.class).index();
			}

			@Override
			public HttpRequest generate(ActionPath<Object> path) {
				return null;
			}
		});
	}

}
