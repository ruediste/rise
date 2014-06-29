package laf.component.html;

import laf.base.BaseModule;
import laf.component.core.ComponentCoreModule;
import laf.http.HttpModule;

import org.jabsaw.Module;

@Module(description = "HTML related functionality of the component framework", imported = { BaseModule.class }, exported = {
		HttpModule.class, ComponentCoreModule.class })
public class ComponentHtmlModule {

}
