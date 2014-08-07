package laf.core.http.requestProcessing;

import laf.base.BaseModule;
import laf.core.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

/**
 *
 */
@Module(description = "processing logic for HTTP requests", imported = {
		BaseModule.class, RequestProcessingModule.class })
public class HttpRequestProcessingModule {

}
