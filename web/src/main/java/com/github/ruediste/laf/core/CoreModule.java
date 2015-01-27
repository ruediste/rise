package com.github.ruediste.laf.core;

import org.jabsaw.Module;

import com.github.ruediste.laf.core.argumentSerializer.CoreArgumentSerializerModule;
import com.github.ruediste.laf.core.base.CoreBaseModule;
import com.github.ruediste.laf.core.http.CoreHttpModule;
import com.github.ruediste.laf.core.persistence.PersistenceModule;
import com.github.ruediste.laf.core.requestParserChain.CoreRequestParserChainModule;
import com.github.ruediste.laf.core.web.resource.StaticWebResourceModule;

@Module(exported = { CoreArgumentSerializerModule.class, CoreBaseModule.class,
		PersistenceModule.class, CoreRequestParserChainModule.class,
		CoreHttpModule.class, StaticWebResourceModule.class })
public class CoreModule {

}
