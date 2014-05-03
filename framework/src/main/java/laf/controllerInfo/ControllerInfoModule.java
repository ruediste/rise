package laf.controllerInfo;

import laf.attachedProperties.AttachedPropertiesModule;
import laf.base.BaseModule;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

@Module(exported = AttachedPropertiesModule.class, imported = {
		InitializationModule.class, BaseModule.class })
public class ControllerInfoModule {

}
