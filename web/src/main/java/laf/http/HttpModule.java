package laf.http;

import laf.base.BaseModule;
import laf.http.request.HttpRequestModule;
import laf.http.requestMapping.HttpRequestMappingModule;

import org.jabsaw.Module;

@Module(description = "Dependencies for Controllers serving HTTP requests", imported = {
		BaseModule.class, HttpRequestMappingModule.class }, exported = { HttpRequestModule.class })
public class HttpModule {

}
