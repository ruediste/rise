package sampleApp;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import laf.component.core.api.CController;
import laf.component.core.binding.BindingGroup;
import laf.component.web.CWControllerUtil;
import laf.core.base.ActionResult;
import laf.core.persistence.PersistenceHelper;
import laf.core.web.annotation.ActionPath;
import sampleApp.entities.User;

@CController
public class SampleComponentController {

	@Inject
	Repo repo;

	@Inject
	Validator validator;

	@Inject
	CWControllerUtil util;

	@Inject
	EntityManager manager;

	@Inject
	PersistenceHelper ph;

	private BindingGroup<User> userBinding = new BindingGroup<>(User.class);

	public User user() {
		return userBinding.proxy();
	}

	@ActionPath("/")
	public ActionResult index() {
		List<User> users = ph.loadAll(User.class);
		User user;
		if (users.isEmpty()) {
			user = new User();
		} else {
			user = users.get(0);
		}

		userBinding.set(user);
		return null;
	}

	public String getSampleText() {
		return "Hello World";
	}

	public void save() {
		userBinding.pushDown();
		Set<ConstraintViolation<User>> res = validator.validate(userBinding
				.get());
		util.setConstraintViolations(userBinding, res);
		if (res.isEmpty()) {
			if (!manager.contains(user())) {
				manager.persist(user());
			}
			util.commit();
		}
	}

	public void reload() {
		if (manager.contains(user())) {
			manager.refresh(user());
		} else {
			userBinding.set(new User());
		}
		userBinding.pullUp();
		util.clearConstraintViolations(userBinding);
	}
}
