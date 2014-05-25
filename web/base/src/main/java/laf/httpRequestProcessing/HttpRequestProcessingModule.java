package laf.httpRequestProcessing;

import javax.inject.Singleton;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.httpRequest.HttpRequestModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "processing logic for HTTP requests", imported = {
		RequestProcessingModule.class, BaseModule.class,
		ActionPathModule.class, }, exported = { HttpRequestModule.class })
public class HttpRequestProcessingModule {

}
