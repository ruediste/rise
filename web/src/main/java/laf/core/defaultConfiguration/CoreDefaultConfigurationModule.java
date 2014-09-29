package laf.core.defaultConfiguration;

import laf.core.CoreModule;
import laf.core.argumentSerializer.defaultSerializers.idSerializers.IdSerializersModule;

import org.jabsaw.Module;

@Module(exported = { CoreModule.class, IdSerializersModule.class })
public class CoreDefaultConfigurationModule {

}
