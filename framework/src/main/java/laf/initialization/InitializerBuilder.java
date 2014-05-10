package laf.initialization;

import laf.initialization.InitializationService.InitializerBuilderImpl;

public interface InitializerBuilder {

	public abstract InitializerBuilderImpl after(Class<?> cls);

	public abstract InitializerBuilderImpl afterOptional(Class<?> cls);

	public abstract InitializerBuilderImpl before(Class<?> cls);

	public abstract InitializerBuilderImpl beforeOptional(Class<?> cls);

	public abstract void from(Object object);

}