package com.github.ruediste.laf.core.guice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Module enabling {@link PostConstruct} annotations
 */
public class PostConstructModule extends AbstractModule{

	@Override
	protected void configure() {
		bindListener(Matchers.any(), new TypeListener(){

			@Override
			public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
				Class<? super I> cl = type.getRawType();
				ArrayList<Method> methods=new ArrayList<>();
				System.out.println(type);
				while (cl!=null){
					for (Method m: cl.getDeclaredMethods()){
						if (m.isAnnotationPresent(PostConstruct.class)){
							m.setAccessible(true);
							methods.add(m);
						}
					}
					cl=cl.getSuperclass();
				}
				if (!methods.isEmpty())
				encounter.register(new InjectionListener<I>() {

					@Override
					public void afterInjection(I injectee) {
						for (Method m: methods){
							try {
								m.invoke(injectee);
							} catch (IllegalAccessException
									| IllegalArgumentException e) {
								throw new RuntimeException("Error calling @PostConstruct method "+m,e);
							} catch (InvocationTargetException e) {
								throw new RuntimeException("Error in @PostConstruct method "+m,e.getCause());
							}
						}
					}
				});
			}
			
		});
	}

}
