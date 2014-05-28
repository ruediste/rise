package sampleApp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import laf.actionPath.ActionPath;
import laf.actionPath.ActionPathFactory;
import laf.configuration.ConfigurationDefiner;
import laf.configuration.DiscoverConfigruationEvent;
import laf.defaultConfiguration.DefaultConfiguration;
import laf.httpRequest.HttpRequest;
import laf.httpRequestMapping.HttpRequestMappingRule;
import laf.httpRequestMapping.HttpRequestMappingRules;
import laf.httpRequestMapping.parameterValueProvider.ParameterValueProvider;

@ApplicationScoped
public class SampleAppConfigurationDefiner implements ConfigurationDefiner {

	@Inject
	DefaultConfiguration defaultConfiguration;

	protected void registerConfigurationValueProviders(
			@Observes DiscoverConfigruationEvent e) {
		e.addPropretiesFile("configuration.properties");
		e.add(this);
		e.add(defaultConfiguration);
	}

	ActionPathFactory pathFactory;

	public void produce(HttpRequestMappingRules rules) {
		defaultConfiguration.produce(rules);
		rules.get().add(new HttpRequestMappingRule() {

			@SuppressWarnings("unchecked")
			@Override
			public ActionPath<ParameterValueProvider> parse(HttpRequest request) {
				return (ActionPath<ParameterValueProvider>) pathFactory
						.buildActionPath(null)
						.controller(SampleComponentController.class).index();
			}

			@Override
			public HttpRequest generate(ActionPath<Object> path) {
				return null;
			}
		});
	}

}
