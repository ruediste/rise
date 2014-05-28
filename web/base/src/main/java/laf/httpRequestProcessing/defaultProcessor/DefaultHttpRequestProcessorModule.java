package laf.httpRequestProcessing.defaultProcessor;

import javax.inject.Singleton;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.httpRequest.HttpRequestModule;
import laf.httpRequestMapping.HttpRequestMappingModule;
import laf.httpRequestProcessing.HttpRequestProcessingModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "Default implementation of a HttpRequestProcessor", imported = {
		BaseModule.class, RequestProcessingModule.class,
		HttpRequestMappingModule.class, ActionPathModule.class }, exported = {
		HttpRequestProcessingModule.class, HttpRequestModule.class })
public class DefaultHttpRequestProcessorModule {

}
