package laf.mvc;

import laf.actionPath.ActionPathModule;
import laf.base.BaseModule;
import laf.persistence.PersistenceModule;
import laf.requestProcessing.RequestProcessingModule;

import org.jabsaw.Module;

@Module(description = "Base module of the MVC framework", imported = {
		BaseModule.class, RequestProcessingModule.class,
		ActionPathModule.class, PersistenceModule.class })
public class MvcModule {

}
