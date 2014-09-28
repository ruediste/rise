package laf.testApp.component;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import laf.component.core.api.CController;
import laf.component.core.binding.BindingGroup;
import laf.component.web.CWControllerUtil;
import laf.core.base.ActionResult;
import laf.core.persistence.PersistenceHelper;
import laf.testApp.dom.TestEntity;

@CController
public class TestComponentController {

	@Inject
	private BindingGroup<TestEntity> binding;

	@Inject
	PersistenceHelper ph;

	@Inject
	EntityManager em;

	@Inject
	CWControllerUtil util;

	public TestEntity entity() {
		return binding.proxy();
	}

	public ActionResult index() {
		List<TestEntity> entities = ph.loadAll(TestEntity.class);
		TestEntity entity;
		if (entities.isEmpty()) {
			entity = new TestEntity();
		} else {
			entity = entities.get(0);
		}
		binding.set(entity);

		return null;
	}

	public void save() {
		binding.pushDown();
		TestEntity entity = binding.get();
		if (!em.contains(entity)) {
			em.persist(entity);
		}

		util.commit();
	}
}
