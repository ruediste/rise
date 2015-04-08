package sampleApp;

import com.github.ruediste.laf.core.entry.ApplicationInstanceBase;
import com.github.ruediste.laf.core.entry.ApplicationInstanceModule;
import com.github.ruediste.laf.core.entry.LoggerModule;
import com.github.ruediste.salta.jsr330.Salta;

public class SampleApplicationInstance extends ApplicationInstanceBase {

	@Override
	protected void startImpl() {
		Salta.createInjector(new ApplicationInstanceModule(),
				new LoggerModule()).injectMembers(this);
	}

}
