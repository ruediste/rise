package laf.controllerInfo;

import laf.attachedProperties.AttachedPropertiesModule;
import laf.base.BaseModule;
import laf.initialization.laf.LafInitializationModule;

import org.jabsaw.Module;

@Module(exported = AttachedPropertiesModule.class, imported = {
	LafInitializationModule.class, BaseModule.class })
public class ControllerInfoModule {

}
