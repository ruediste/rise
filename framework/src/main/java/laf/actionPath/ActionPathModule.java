package laf.actionPath;

import laf.attachedProperties.AttachedPropertiesModule;
import laf.base.BaseModule;
import laf.controllerInfo.ControllerInfoModule;

import org.jabsaw.Module;

@Module(exported = ControllerInfoModule.class, imported = {
		AttachedPropertiesModule.class, BaseModule.class })
public class ActionPathModule {

}
