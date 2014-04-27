package laf.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;

import laf.configuration.ConfigurationParameterRepository.ParameterEntry;

import org.slf4j.Logger;

/**
 * Scans all singletons for {@link ConfigurationParameter} definitions
 */
@Singleton
public class ConfigurationParameterScanner {
	@Inject
	BeanManager beanManager;

	@Inject
	Instance<Object> instance;

	@Inject
	Logger log;

	public Collection<ParameterEntry> scan() {
		ArrayList<ParameterEntry> result = new ArrayList<>();

		for (Bean<?> bean : beanManager.getBeans(Object.class)) {
			if (!bean.getBeanClass().isAnnotationPresent(Singleton.class)) {
				continue;
			}
			Object beanInstance = null;
			for (Field f : bean.getBeanClass().getFields()) {
				if (ConfigurationParameter.class.isAssignableFrom(f.getType())) {
					log.trace("Handling parameter "
							+ bean.getBeanClass().getName() + "." + f.getName()
							+ ": " + f.getGenericType());

					// obtain bean instance if necessary
					if (beanInstance == null) {
						beanInstance = instance.select(bean.getBeanClass())
								.get();
					}

					try {
						ConfigurationParameter<?> p = (ConfigurationParameter<?>) f
								.get(beanInstance);

						// add to the result
						result.add(new ParameterEntry(p, f, beanInstance));

					} catch (IllegalArgumentException | IllegalAccessException e1) {
						throw new Error(
								"error while accessing configuration parameter "
										+ f.getName() + " in singleton "
										+ bean.getBeanClass().getName());
					}
				}
			}
		}

		return result;
	}
}
