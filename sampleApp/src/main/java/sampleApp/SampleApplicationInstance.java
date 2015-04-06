package sampleApp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ruediste.laf.core.defaultConfiguration.DefaultConfiguration;
import com.github.ruediste.laf.core.entry.ApplicationInstance;
import com.github.ruediste.laf.core.entry.ApplicationInstanceModule;
import com.github.ruediste.laf.core.entry.HttpMethod;
import com.github.ruediste.laf.core.entry.LoggerModule;
import com.github.ruediste.salta.jsr330.Salta;

public class SampleApplicationInstance extends ApplicationInstance {

	@Inject
	DefaultConfiguration config;

	@Override
	protected void startImpl() {
		Salta.createInjector(new ApplicationInstanceModule(),
				new LoggerModule()).injectMembers(this);
	}

	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse response, HttpMethod method)
			throws IOException, ServletException {
		PrintWriter writer = response.getWriter();
		writer.write("Hello World");
		writer.close();
	}

}
