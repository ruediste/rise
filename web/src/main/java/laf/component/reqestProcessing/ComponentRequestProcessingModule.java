package laf.component.reqestProcessing;

import laf.base.BaseModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Component specific request processing", imported = {
		BaseModule.class, RequestProcessingModule.class })
public class ComponentRequestProcessingModule {

}
