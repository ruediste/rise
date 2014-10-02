package laf.skeleton.sample;

import java.util.List;

import javax.inject.Inject;

import laf.component.core.binding.BindingGroup;
import laf.core.base.ActionResult;
import laf.core.persistence.PersistenceHelper;
import laf.skeleton.base.ComponentControllerBase;
import laf.skeleton.dom.SampleEntity;

public class SampleComponentController extends ComponentControllerBase {

	@Inject
	private BindingGroup<SampleEntity> entityBinding;

	@Inject
	private BindingGroup<Message> messageBinding;

	@Inject
	PersistenceHelper ph;

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

	public SampleEntity entity() {
		return entityBinding.proxy();
	}

	public ActionResult index() {
		List<SampleEntity> entities = ph.loadAll(SampleEntity.class);
		SampleEntity entity;
		if (entities.isEmpty()) {
			entity = new SampleEntity();
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
		SampleEntity entity = entityBinding.get();
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
				SampleEntity e2 = em.find(SampleEntity.class, entity.getId());
				messageBinding.set(new Message(e2.getStringValue()));
			}
		});

		messageBinding.pullUp();
	}
}
