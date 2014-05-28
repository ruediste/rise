package laf;

import laf.base.BaseModule;
import laf.httpRequestProcessing.HttpRequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Entry point to the LAF framework", imported = {
		HttpRequestProcessingModule.class, BaseModule.class })
public class LafModule {

}
