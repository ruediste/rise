package sampleApp;

import javax.servlet.annotation.WebServlet;

import com.github.ruediste.laf.core.entry.ApplicationInstance;
import com.github.ruediste.laf.core.entry.ApplicationModule;
import com.github.ruediste.laf.core.entry.FrontServletBase;
import com.github.ruediste.laf.core.entry.LoggerModule;
import com.github.ruediste.salta.jsr330.Salta;

@WebServlet(value = "/front/*", loadOnStartup = 10)
public class FrontServlet extends FrontServletBase {

	private static final long serialVersionUID = 1L;

	@Override
	protected Class<? extends ApplicationInstance> getApplicationInstanceClass() {
		return SampleApplicationInstance.class;
	}

	@Override
	protected void initImpl() throws Exception {
		Salta.createInjector(new ApplicationModule(), new LoggerModule())
				.injectMembers(this);
	}

}
