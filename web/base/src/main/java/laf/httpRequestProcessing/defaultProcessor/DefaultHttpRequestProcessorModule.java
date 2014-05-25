package laf.httpRequestProcessing.defaultProcessor;

import javax.inject.Singleton;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestProcessing.HttpRequestProcessingModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "Default implementation of a HttpRequestProcessor", imported = {
		HttpRequestProcessingModule.class, BaseModule.class,
		RequestProcessingModule.class, HttpRequestMappingModule.class,
		ActionPathModule.class })
public class DefaultHttpRequestProcessorModule {

}
