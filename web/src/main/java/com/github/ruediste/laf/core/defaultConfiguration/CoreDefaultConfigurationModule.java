package com.github.ruediste.laf.core.defaultConfiguration;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.CoreModule;
import com.github.ruediste.laf.core.argumentSerializer.defaultSerializers.idSerializers.IdSerializersModule;

@Module(exported = { CoreModule.class, IdSerializersModule.class })
public class CoreDefaultConfigurationModule {

}
