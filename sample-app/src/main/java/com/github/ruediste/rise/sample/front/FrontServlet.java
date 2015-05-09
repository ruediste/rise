package com.github.ruediste.rise.sample.front;

import java.util.Properties;

import com.github.ruediste.rise.core.front.FrontServletBase;
import com.github.ruediste.rise.core.persistence.BitronixDataSourceFactory;
import com.github.ruediste.rise.core.persistence.BitronixModule;
import com.github.ruediste.rise.core.persistence.EclipseLinkEntityManagerFactoryProvider;
import com.github.ruediste.rise.core.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.rise.core.persistence.PersistenceModuleUtil;
import com.github.ruediste.rise.integration.PermanentIntegrationModule;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Salta;

public class FrontServlet extends FrontServletBase {
	public FrontServlet() {
		super(App.class);
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void initImpl() throws Exception {
		Salta.createInjector(
				new AbstractModule() {

					@Override
					protected void configure() throws Exception {
						PersistenceModuleUtil.bindDataSource(binder(), null,
								new EclipseLinkEntityManagerFactoryProvider(
										"sampleApp"),
								new BitronixDataSourceFactory(
										new H2DatabaseIntegrationInfo()) {

									@Override
									protected void initializeProperties(
											Properties props) {
										props.setProperty("URL",
												"jdbc:h2:file:~/sampleApp;DB_CLOSE_DELAY=-1;MVCC=false");
										props.setProperty("user", "sa");
										props.setProperty("password", "sa");
									}
								});
					}
				}, new BitronixModule(),
				new PermanentIntegrationModule(getServletConfig()))
				.injectMembers(this);
	}

}
