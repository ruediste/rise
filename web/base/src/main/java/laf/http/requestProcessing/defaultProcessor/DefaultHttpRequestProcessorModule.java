package laf.http.requestProcessing.defaultProcessor;

import javax.inject.Singleton;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.http.HttpModule;
import laf.http.request.HttpRequestModule;
import laf.http.requestMapping.HttpRequestMappingModule;
import laf.http.requestProcessing.HttpRequestProcessingModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "Default implementation of a HttpRequestProcessor", imported = {
		BaseModule.class, RequestProcessingModule.class,
		HttpRequestMappingModule.class, ActionPathModule.class }, exported = {
		HttpRequestProcessingModule.class, HttpRequestModule.class,
		HttpModule.class, })
public class DefaultHttpRequestProcessorModule {

}
