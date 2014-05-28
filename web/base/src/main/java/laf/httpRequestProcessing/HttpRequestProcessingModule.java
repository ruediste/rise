package laf.httpRequestProcessing;

import javax.inject.Singleton;

import laf.base.BaseModule;

import org.jabsaw.Module;

@Singleton
@Module(description = "processing logic for HTTP requests", imported = { BaseModule.class })
public class HttpRequestProcessingModule {

}
