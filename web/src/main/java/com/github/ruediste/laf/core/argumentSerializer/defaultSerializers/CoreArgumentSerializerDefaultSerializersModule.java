package com.github.ruediste.laf.core.argumentSerializer.defaultSerializers;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.persistence.PersistenceModule;

@Module(exported = {
		com.github.ruediste.laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		PersistenceModule.class })
public class CoreArgumentSerializerDefaultSerializersModule {

}
