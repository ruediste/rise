package com.github.ruediste.rise.testApp.component;

import static org.rendersnake.HtmlAttributesFactory.class_;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.github.ruediste.rise.api.ViewComponent;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CGroup;
import com.github.ruediste.rise.component.components.CRender;
import com.github.ruediste.rise.component.components.CTextFieldFormGroup;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.OwnEntityManagers;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.testApp.persistence.TestEntity;

@OwnEntityManagers
public class SampleSubController {

	public static class View extends ViewComponent<SampleSubController> {

		@Override
		protected Component createComponents() {
			return new CGroup()

					.add(new CRender(html -> html.span(class_("value"))
							.content(controller.entity().getValue())))

					.add(new CButton("refresh").class_("refresh").handler(
							controller::refresh))

					.add(new CButton("save").class_("save").handler(
							controller::save))

					.add(new CTextFieldFormGroup().bind(g -> g
							.setText(controller.entity().getValue())));
		}
	}

	@Inject
	EntityManager em;
	@Inject
	ComponentUtil util;

	@Inject
	private BindingGroup<TestEntity> entity;

	public TestEntity entity() {
		return entity.proxy();
	}

	@Inject
	EntityManagerHolder holder;

	public void initialize(long id) {
		TestEntity e = em.find(TestEntity.class, id);
		if (e == null)
			throw new RuntimeException("TestEntity " + id + " not found");
		entity.set(e);
	}

	public void refresh() {
		em.refresh(entity.get());
		entity.pullUp();
	}

	public void save() {
		entity.pushDown();
		util.commit();
	}
}
