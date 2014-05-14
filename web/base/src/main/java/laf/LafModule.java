package laf;

import laf.requestProcessing.http.HttpRequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Entry point to the LAF framework", imported = { HttpRequestProcessingModule.class })
public class LafModule {

}
