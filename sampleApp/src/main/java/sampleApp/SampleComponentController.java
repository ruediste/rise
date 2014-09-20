package sampleApp;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import laf.component.core.api.CController;
import laf.component.core.binding.BindingGroup;
import laf.component.web.CWControllerUtil;
import laf.core.base.ActionResult;
import sampleApp.entities.User;

@CController
public class SampleComponentController {

	@Inject
	Repo repo;

	@Inject
	Validator validator;

	@Inject
	CWControllerUtil util;

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
		Set<ConstraintViolation<User>> res = validator.validate(binding.get());
		util.setConstraintViolations(binding, res);
	}

	public void reload() {
		binding.pullUp();
		util.clearConstraintViolations(binding);
	}
}
