package laf.mvc.html;

import laf.base.BaseModule;
import laf.core.http.HttpModule;
import laf.mvc.MvcModule;

import org.jabsaw.Module;

@Module(description = "HTML related part of the MVC framework", exported = {
		MvcModule.class, BaseModule.class, HttpModule.class })
public class MvcHtmlModule {

}
