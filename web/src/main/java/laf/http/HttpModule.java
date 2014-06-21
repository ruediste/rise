package laf.http;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.http.request.HttpRequestModule;
import laf.http.requestMapping.HttpRequestMappingModule;

import org.jabsaw.Module;

@Module(description = "Dependencies for Controllers serving HTTP requests", imported = {
		BaseModule.class, ActionPathModule.class,
		HttpRequestMappingModule.class }, exported = { HttpRequestModule.class })
public class HttpModule {

}
