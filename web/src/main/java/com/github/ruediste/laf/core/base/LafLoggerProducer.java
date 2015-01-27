package com.github.ruediste.laf.core.base;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.*;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producer for {@link Logger} and {@link LafLogger} instances. 
 */
public class LafLoggerProducer {
	/**
	 * Serializable handler for the {@link Logger} interface
	 */
	private static class Handler implements InvocationHandler, Serializable {
		private static final long serialVersionUID = 1L;
		private Class<?> cls;
		private transient Logger log;

		public Handler(Class<?> cls) {
			this.cls = cls;
			log = LoggerFactory.getLogger(cls);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return method.invoke(log, args);
		}

		private void readObject(java.io.ObjectInputStream in)
				throws IOException, ClassNotFoundException {
			in.defaultReadObject();
			log = LoggerFactory.getLogger(cls);
		}
	}

	public LafLogger getLogger(Object obj){
		return getLogger(obj.getClass());
	}
	
	public LafLogger getLogger(Class<?> cls){
		return (LafLogger) Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), new Class<?>[] { LafLogger.class },
				new Handler(cls));
	}
	
	@Produces
	@Typed(LafLogger.class)
	 LafLogger produce(InjectionPoint point) {
		return getLogger(point.getMember().getDeclaringClass());
	}
	
	@Produces
	@Typed(Logger.class)
	Logger produceLogger(InjectionPoint point) {
		return LoggerFactory.getLogger(point.getMember().getDeclaringClass());
	}
}
