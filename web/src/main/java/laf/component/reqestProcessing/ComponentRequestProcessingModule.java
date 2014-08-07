package laf.component.reqestProcessing;

import laf.base.BaseModule;
import laf.component.pageScope.PageScopeModule;
import laf.core.persistence.PersistenceModule;
import laf.core.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Component specific request processing", imported = {
		BaseModule.class, RequestProcessingModule.class, PageScopeModule.class,
		PersistenceModule.class })
public class ComponentRequestProcessingModule {

}
