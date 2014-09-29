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
	private BindingGroup<TestEntity> entityBinding;

	@Inject
	private BindingGroup<Message> messageBinding;

	@Inject
	PersistenceHelper ph;

	@Inject
	EntityManager em;

	@Inject
	CWControllerUtil util;

	public class Message {
		private String message;

		public Message(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public Message message() {
		return messageBinding.proxy();
	}

	public TestEntity entity() {
		return entityBinding.proxy();
	}

	public ActionResult index() {
		List<TestEntity> entities = ph.loadAll(TestEntity.class);
		TestEntity entity;
		if (entities.isEmpty()) {
			entity = new TestEntity();
		} else {
			entity = entities.get(0);
		}
		entityBinding.set(entity);
		messageBinding.set(new Message(""));

		return null;
	}

	public void pushDown() {
		entityBinding.pushDown();
	}

	public void save() {
		entityBinding.pushDown();
		TestEntity entity = entityBinding.get();
		boolean isNew;
		if (!em.contains(entity)) {
			isNew = true;
			em.persist(entity);
		} else {
			isNew = false;
		}

		util.checkAndCommit(() -> {
			if (isNew) {
				messageBinding.set(new Message("<null>"));
			} else {
				TestEntity e2 = em.find(TestEntity.class, entity.getId());
				messageBinding.set(new Message(e2.getStringValue()));
			}
		});

		messageBinding.pullUp();
	}
}
