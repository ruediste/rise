package laf.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationParameterRepository.ParameterEntry;
import laf.initialization.*;

import org.slf4j.Logger;

@Singleton
public class ConfigurationInitializerCreator {

	@Inject
	ConfigurationModule configurationModule;

	@Inject
	ConfigurationParameterRepository configurationParameterRepository;

	@Inject
	Logger log;

	/**
	 * An initializer which depends on all initializers created through
	 * {@link ConfigurationParameter}s
	 */
	class ConfigurationInitializer implements Initializer {

		Set<Class<?>> relatedRepresentingClasses = new HashSet<>();

		Set<Initializer> requiredInitializers = new HashSet<>();

		public ConfigurationInitializer() {
			relatedRepresentingClasses.add(FrameworkRootInitializer.class);
		}

		public void addInitializer(Initializer i) {
			requiredInitializers.add(i);
			relatedRepresentingClasses.add(i.getRepresentingClass());
		}

		@Override
		public Collection<InitializerDependsRelation> getDeclaredRelations(
				Initializer other) {

			// make the framework root initializer depend on this initializer
			if (other.getRepresentingClass().equals(
					FrameworkRootInitializer.class)) {
				return Collections.singleton(new InitializerDependsRelation(
						other, this, false));
			}
			if (requiredInitializers.contains(other)) {
				return Collections.singleton(new InitializerDependsRelation(
						this, other, false));
			} else {
				return Collections.emptyList();
			}
		}

		@Override
		public Set<Class<?>> getRelatedRepresentingClasses() {
			return relatedRepresentingClasses;
		}

		@Override
		public Class<?> getRepresentingClass() {
			return ConfigurationInitializer.class;
		}

		@Override
		public void run() {
			// nothing to do
		}

		@Override
		public String toString() {
			return "ConfigurationInitializer";
		}

	}

	public void initialize(@Observes CreateInitializersEvent e) {
		configurationModule.checkInitialized();

		log.debug("Scanning configuration parameters in singletons for initializers");
		ConfigurationInitializer configurationInitializer = new ConfigurationInitializer();
		e.addInitializer(configurationInitializer);

		for (ParameterEntry entry : configurationParameterRepository
				.getEntries()) {
			// add eventual initializers
			{
				for (Initializer i : e.createInitializersFrom(entry.parameter
						.getValue())) {
					log.debug("Found initializer " + i);
					configurationInitializer.addInitializer(i);
				}
			}
		}
	}

}
