package laf.controllerInfo;

import laf.attachedProperties.AttachedPropertiesModule;
import laf.initialization.InitializationModule;

import org.jabsaw.Module;

@Module(exported = AttachedPropertiesModule.class, imported = { InitializationModule.class })
public class ControllerInfoModule {

}
