package laf.base;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.*;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@Produces
	@Typed(LafLogger.class)
	public LafLogger produce(InjectionPoint point) {
		return (LafLogger) Proxy.newProxyInstance(this.getClass()
				.getClassLoader(), new Class<?>[] { LafLogger.class },
				new Handler(point.getMember().getDeclaringClass()));
	}
}
