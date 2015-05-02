package com.github.ruediste.laf.testApp;

import java.util.Properties;

import javax.servlet.Servlet;

import com.github.ruediste.laf.core.persistence.BitronixDataSourceFactory;
import com.github.ruediste.laf.core.persistence.BitronixModule;
import com.github.ruediste.laf.core.persistence.EclipseLinkEntityManagerFactoryProvider;
import com.github.ruediste.laf.core.persistence.H2DatabaseIntegrationInfo;
import com.github.ruediste.laf.core.persistence.PersistenceModuleUtil;
import com.github.ruediste.laf.integration.PermanentIntegrationModule;
import com.github.ruediste.laf.test.WebTestBase;
import com.github.ruediste.laf.testApp.app.TestAppFrontServlet;
import com.github.ruediste.laf.testApp.app.TestDynamicApplication;
import com.github.ruediste.salta.jsr330.AbstractModule;
import com.github.ruediste.salta.jsr330.Injector;
import com.github.ruediste.salta.jsr330.Salta;

public class WebTest extends WebTestBase {

	@Override
	protected final Servlet createServlet(Object testCase) {
		TestDynamicApplication app = new TestDynamicApplication() {

			@Override
			protected void startImpl(Injector permanentInjector) {
				super.startImpl(permanentInjector);
				injectMembers(testCase);
			}
		};

		Servlet frontServlet = new TestAppFrontServlet(app) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void initImpl() throws Exception {
				Salta.createInjector(
						new AbstractModule() {

							@Override
							protected void configure() throws Exception {
								PersistenceModuleUtil.bindDataSource(
										binder(),
										null,
										new EclipseLinkEntityManagerFactoryProvider(
												"testApp"),
										new BitronixDataSourceFactory(
												new H2DatabaseIntegrationInfo()) {

											@Override
											protected void initializeProperties(
													Properties props) {
												props.setProperty("URL",
														"jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=false");
												props.setProperty("user", "sa");
												props.setProperty("password",
														"sa");
											}
										});
							}
						}, new BitronixModule(),
						new PermanentIntegrationModule(getServletConfig()))
						.injectMembers(this);
			}
		};
		return frontServlet;
	}
}
