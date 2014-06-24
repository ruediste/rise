package laf;

import laf.base.BaseModule;
import laf.http.HttpModule;
import laf.http.requestProcessing.HttpRequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Entry point to the LAF framework", imported = {
		HttpRequestProcessingModule.class, BaseModule.class, HttpModule.class })
public class LafModule {

}
