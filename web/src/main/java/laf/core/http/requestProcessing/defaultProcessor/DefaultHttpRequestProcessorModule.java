package laf.core.http.requestProcessing.defaultProcessor;

import javax.inject.Singleton;

import laf.base.BaseModule;
import laf.core.http.HttpModule;
import laf.core.http.request.HttpRequestModule;
import laf.core.http.requestMapping.HttpRequestMappingModule;
import laf.core.http.requestProcessing.HttpRequestProcessingModule;
import laf.core.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "Default implementation of a HttpRequestProcessor", imported = {
		BaseModule.class, RequestProcessingModule.class,
		HttpRequestMappingModule.class }, exported = {
		HttpRequestProcessingModule.class, HttpRequestModule.class,
		HttpModule.class, })
public class DefaultHttpRequestProcessorModule {

}
