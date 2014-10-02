package laf.core;

import laf.core.argumentSerializer.CoreArgumentSerializerModule;
import laf.core.base.CoreBaseModule;
import laf.core.http.CoreHttpModule;
import laf.core.persistence.PersistenceModule;
import laf.core.requestParserChain.CoreRequestParserChainModule;
import laf.core.web.resource.StaticWebResourceModule;

import org.jabsaw.Module;

@Module(exported = { CoreArgumentSerializerModule.class, CoreBaseModule.class,
		PersistenceModule.class, CoreRequestParserChainModule.class,
		CoreHttpModule.class, StaticWebResourceModule.class })
public class CoreModule {

}
