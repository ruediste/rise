package laf.core.argumentSerializer.defaultSerializers;

import laf.core.persistence.PersistenceModule;

import org.jabsaw.Module;

@Module(exported = {
		laf.core.argumentSerializer.CoreArgumentSerializerModule.class,
		PersistenceModule.class })
public class CoreArgumentSerializerDefaultSerializersModule {

}
