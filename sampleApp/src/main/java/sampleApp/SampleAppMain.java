package sampleApp;

import com.github.ruediste.laf.core.entry.StandaloneLafApplication;

public class SampleAppMain {

	public static void main(String[] args) {
		new StandaloneLafApplication().start(FrontServlet.class);
	}

}
