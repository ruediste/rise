package laf;

import laf.base.BaseModule;
import laf.http.request.HttpRequestModule;
import laf.http.requestProcessing.HttpRequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Entry point to the LAF framework", imported = {
		HttpRequestProcessingModule.class, BaseModule.class,
		HttpRequestModule.class })
public class LafModule {

}
