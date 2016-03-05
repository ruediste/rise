package com.github.ruediste.rise.crud;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.FrameworkViewComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CButtonTemplate;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CDataGrid.Cell;
import com.github.ruediste.rise.component.components.CDataGrid.Column;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.PersistentType;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.crud.CrudUtil.CrudList;
import com.github.ruediste.rise.crud.CrudUtil.PersistenceFilterContext;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.MembersLabeled;
import com.google.common.base.Preconditions;

public class DefaultCrudListController extends SubControllerComponent implements CrudList {
	@Inject
	CrudReflectionUtil crudReflectionUtil;

	@Inject
	CrudPropertyFilters filters;

	@Inject
	ComponentUtil componentUtil;

	@Inject
	CrudUtil crudUtil;

	@SuppressWarnings("unused")
	private static class DefaultCrudBrowserView extends FrameworkViewComponent<DefaultCrudListController> {

		@MembersLabeled
		enum Labels {
			FILTER, ACTIONS
		}

		@Inject
		LabelUtil labelUtil;

		@Override
		protected Component createComponents() {

			// create columns
			ArrayList<Column<Object>> columns = new ArrayList<>();
			for (CrudPropertyInfo p : controller.columnProperties) {
				PropertyInfo property = p.getProperty();
				columns.add(new Column<>(() -> new CDataGrid.Cell(labelUtil.property(property).label()),
						item -> new Cell(new CText(Objects.toString(property.getValue(item)))))
								.TEST_NAME(property.getName()));
			}
			if (controller.getItemActionsFactory() != null)
				columns.add(new Column<Object>(() -> new Cell(new CText(label(Labels.ACTIONS))),
						controller.getItemActionsFactory()).TEST_NAME("actions"));

			// @formatter:off
			return toComponent(html -> html.div().CLASS("panel panel-default").div().CLASS("panel-heading")
					.content(Labels.FILTER).div().CLASS("panel-body").fForEach(controller.filterList, filter -> {
						html.add(filter.getComponent());
					})._div()._div()
					.add(new CButton(controller, x -> x.search()).apply(CButtonTemplate.setArgs(x -> x.primary())))
					.add(new CDataGrid<Object>().TEST_NAME("resultList").setColumns(columns)
							.bindOneWay(g -> g.setItems(controller.data().getItems())))
					.div().TEST_NAME("bottom-actions")
					.fIf(controller.bottomActions != null, () -> html.add(controller.bottomActions))._div());
			// @formatter:on
		}
	}

	@Inject
	EntityManagerHolder emh;

	static class Data {
		private List<Object> items;

		public List<Object> getItems() {
			return items;
		}

		public void setItems(List<Object> objects) {
			this.items = objects;
		}
	}

	private Function<Object, Cell> itemActionsFactory;

	private Component bottomActions;

	public Component getBottomActions() {
		return bottomActions;
	}

	@Override
	public DefaultCrudListController setBottomActions(Component bottomActions) {
		this.bottomActions = bottomActions;
		return this;
	}

	private PersistentType type;

	@Inject
	BindingGroup<Data> data;

	List<CrudPropertyInfo> columnProperties;

	List<CrudPropertyFilter> filterList;

	private Consumer<PersistenceFilterContext<?>> constantFilter;

	Data data() {
		return data.proxy();
	}

	private EntityManager getEm() {
		return emh.getEntityManager(type.getEmQualifier());
	}

	@Labeled
	@GlyphiconIcon(Glyphicon.search)
	public void search() {
		TypedQuery<Object> q = crudUtil.queryWithFilters(type, getEm(), ctx -> {
			if (constantFilter != null)
				constantFilter.accept(ctx);
			for (CrudPropertyFilter filter : filterList) {
				filter.applyFilter(ctx);
			}
		});

		data.get().setItems(q.getResultList());
		data.pullUp();
	}

	public DefaultCrudListController initialize(Class<?> entityClass, Class<? extends Annotation> emQualifier,
			Consumer<PersistenceFilterContext<?>> constantFilter) {
		this.constantFilter = constantFilter;
		Preconditions.checkNotNull(entityClass, "entityClass is null");
		type = crudReflectionUtil.getPersistentType(emQualifier, entityClass);

		columnProperties = crudReflectionUtil.getBrowserProperties(type);
		filterList = columnProperties.stream().map(filters::create).collect(toList());

		search();

		return this;
	}

	public Function<Object, Cell> getItemActionsFactory() {
		return itemActionsFactory;
	}

	@Override
	public DefaultCrudListController setItemActionsFactory(Function<Object, Cell> itemActionsFactory) {
		this.itemActionsFactory = itemActionsFactory;
		return this;
	}

	@Override
	public PersistentType getType() {
		return type;
	}

	@Override
	public void refresh() {
		search();
	}

}