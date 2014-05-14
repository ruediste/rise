package laf.requestProcessing.http.defaultProcessor;

import javax.inject.Singleton;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.requestProcessing.RequestProcessingModule;
import laf.requestProcessing.http.HttpRequestProcessingModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "Default implementation of a HttpRequestProcessor", imported = {
		HttpRequestProcessingModule.class, BaseModule.class,
		RequestProcessingModule.class, HttpRequestMappingModule.class,
		ActionPathModule.class })
public class DefaultHttpRequestProcessorModule {

}
