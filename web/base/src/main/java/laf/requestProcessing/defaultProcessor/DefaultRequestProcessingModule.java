package laf.requestProcessing.defaultProcessor;

import laf.base.BaseModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "default request processing implementation", imported = { BaseModule.class }, exported = RequestProcessingModule.class)
public class DefaultRequestProcessingModule {

}
