package com.github.ruediste.rise.testApp.component;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.testApp.persistence.TestEntity;

public class SampleComponentController extends ControllerComponent {

	@Inject
	SampleSubController subControllerA;

	@Inject
	SampleSubController subControllerB;

	@Inject
	EntityManager em;

	@Inject
	ComponentUtil util;

	TestEntity entity;

	public ActionResult initialize(long id) {
		entity = em.find(TestEntity.class, id);
		subControllerA.initialize(id);
		subControllerB.initialize(id);
		return null;
	}

	public void refresh() {
		em.refresh(entity);
	}
}
