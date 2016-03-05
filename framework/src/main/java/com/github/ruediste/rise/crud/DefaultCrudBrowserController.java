package com.github.ruediste.rise.crud;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CButtonTemplate;
import com.github.ruediste.rise.component.components.CController;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CGroup;
import com.github.ruediste.rise.component.components.CIconLabel;
import com.github.ruediste.rise.component.components.ComponentStackHandle;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.message.TMessage;
import com.github.ruediste1.i18n.message.TMessages;

public class DefaultCrudBrowserController extends SubControllerComponent {

	@Inject
	CrudUtil util;

	@Inject
	CrudReflectionUtil reflectionUtil;

	@Inject
	CrudActionInvocationUtil actionUtil;

	private CrudList ctrl;

	public static class View extends FrameworkViewComponent<DefaultCrudBrowserController> {

		@TMessages
		public interface Messages {

			@TMessage("Browser for {clazz}")
			LString browserFor(LString clazz);

		}

		@Inject
		Messages messages;

		@Override
		protected Component createComponents() {
			return toComponent(
					html -> html.h1().content(messages.browserFor(label(controller.ctrl.getType().getEntityClass())))
							.add(new CController(controller.ctrl)));
		}

	}

	public DefaultCrudBrowserController initialize(Class<?> entityClass, Class<? extends Annotation> emQualifier) {
		ctrl = util.getStrategy(CrudUtil.CrudListFactory.class, entityClass).createList(emQualifier, entityClass, null);

		ctrl.setItemActionsFactory(item -> {
			CGroup group = new CGroup()
					.add(new CButton(go(CrudControllerBase.class).display(item), true)
							.apply(CButtonTemplate.setArgs(x -> x.primary())))
					.add(new CButton(go(CrudControllerBase.class).edit(item), true).apply(CButtonTemplate.setArgs(x -> {
					}))).add(new CButton(go(CrudControllerBase.class).delete(item), true)
							.apply(CButtonTemplate.setArgs(x -> x.danger())));
			reflectionUtil.getBrowseActionMethods(entityClass).forEach(m -> {
				CButton btn = new CButton().add(new CIconLabel().setMethod(m));
				btn.setHandler(() -> actionUtil.invokeActionMethod(m, item, new ComponentStackHandle(btn),
						x -> ctrl.refresh()));
				group.add(btn);
			});
			return new CDataGrid.Cell(group);
		});

		ctrl.setBottomActions(new CButton(go(CrudControllerBase.class).create(entityClass, emQualifier)));

		return this;
	}
}