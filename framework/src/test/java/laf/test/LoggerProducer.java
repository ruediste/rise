package laf.test;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProducer {
	@Produces
	Logger produceUrlMapping(InjectionPoint point) {
		return LoggerFactory.getLogger(point.getMember().getDeclaringClass());
	}
}
