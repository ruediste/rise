package laf.mvc;

import laf.base.BaseModule;
import laf.core.persistence.PersistenceModule;
import laf.core.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Base module of the MVC framework", imported = {
		BaseModule.class, RequestProcessingModule.class,
		PersistenceModule.class })
public class MvcModule {

}
