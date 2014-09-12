package sampleApp;

import laf.component.core.api.CController;
import laf.component.core.binding.BindingGroup;
import laf.core.base.ActionResult;
import sampleApp.entities.User;

@CController
public class SampleComponentController {

	private BindingGroup<User> binding = new BindingGroup<>();

	public User user() {
		return binding.proxy();
	}

	public ActionResult index() {
		User user = new User();
		user.setFistName("John");
		user.setLastName("Smith");
		binding.pullUp(user);
		return null;
	}

	public String getSampleText() {
		return "Hello World";
	}
}
