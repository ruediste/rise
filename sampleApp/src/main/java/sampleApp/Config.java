package sampleApp;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import laf.initialization.LafInitializer;
import laf.initialization.laf.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Config {

	@LafInitializer(phase = LafConfigurationPhase.class, before = FrameworkRootInitializer.class, after = DefaultConfigurationInitializer.class)
	public void initializer() {

	}

	@Produces
	Logger produceUrlMapping(InjectionPoint point) {
		return LoggerFactory.getLogger(point.getMember().getDeclaringClass());
	}
}
