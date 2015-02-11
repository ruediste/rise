package com.github.ruediste.laf.core.guice;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class LoggerBindingModule extends AbstractModule {

	static class Log4JTypeListener implements TypeListener {
		@Override
		public <T> void hear(TypeLiteral<T> typeLiteral,
				TypeEncounter<T> typeEncounter) {
			Class<?> clazz = typeLiteral.getRawType();
			while (clazz != null) {
				for (Field field : clazz.getDeclaredFields()) {
					if (field.getType() == Logger.class) {
						typeEncounter.register(new Log4JMembersInjector<T>(
								field));
					}
				}
				clazz = clazz.getSuperclass();
			}
		}
	}

	static class Log4JMembersInjector<T> implements MembersInjector<T> {
		private final Field field;
		private final Logger logger;

		Log4JMembersInjector(Field field) {
			this.field = field;
			this.logger = LoggerFactory.getLogger(field.getDeclaringClass());
			field.setAccessible(true);
		}

		@Override
		public void injectMembers(T t) {
			try {
				field.set(t, logger);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected void configure() {
		bindListener(Matchers.any(), new Log4JTypeListener());
	}

}
