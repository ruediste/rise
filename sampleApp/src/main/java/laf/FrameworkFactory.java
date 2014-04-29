package laf;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class FrameworkFactory {
	@Produces Logger produceUrlMapping(InjectionPoint point){
		return LoggerFactory.getLogger(point.getMember().getDeclaringClass());
	}
}
