package laf.mvc.web;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import laf.mvc.Controller;

import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ControllerRepository {

	@Inject
	Logger log;

	@Inject
	@Controller
	Instance<Object> controllers;

	@Inject
	BeanManager beanManager;

	private BiMap<Class<?>, String> nameMap = HashBiMap.create();

	public void initialize(Function<Class<?>, String> nameMapper) {
		Controller controller = new Controller() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Controller.class;
			}
		};
		for (Bean<?> bean : beanManager.getBeans(Object.class, controller)) {
			Class<?> cls = bean.getBeanClass();
			nameMap.put(cls, nameMapper.apply(cls));
		}
	}

	public Class<?> getControllerClass(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}
}
