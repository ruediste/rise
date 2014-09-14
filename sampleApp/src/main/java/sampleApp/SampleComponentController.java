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
		return null;
	}

	public String getSampleText() {
		return "Hello World";
	}

	public void save() {
		binding.pushDown(repo.getUser());
	}

	public void hack() {
		binding.pullUp(repo.getUser());
	}

	public void reload() {
		binding.pullUp(repo.getUser());
	}
}
