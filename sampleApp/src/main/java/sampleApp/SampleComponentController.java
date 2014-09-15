package sampleApp;

import javax.inject.Inject;

import laf.component.core.api.CController;
import laf.component.core.binding.BindingGroup;
import laf.core.base.ActionResult;
import sampleApp.entities.User;

@CController
public class SampleComponentController {

	@Inject
	Repo repo;

	private BindingGroup<User> binding = new BindingGroup<>(User.class);

	public User user() {
		return binding.proxy();
	}

	public ActionResult index() {
		binding.set(repo.getUser());
		return null;
	}

	public String getSampleText() {
		return "Hello World";
	}

	public void save() {
		binding.pushDown();
	}

	public void reload() {
		binding.pullUp();
	}
}
