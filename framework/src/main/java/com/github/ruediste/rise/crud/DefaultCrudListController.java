package com.github.ruediste.rise.crud;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.ComponentUtil;
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
import com.github.ruediste.rise.crud.CrudPropertyFilter.CrudFilterPersitenceContext;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.MembersLabeled;
import com.google.common.base.Preconditions;

public class DefaultCrudListController extends SubControllerComponent {
    @Inject
    CrudReflectionUtil crudReflectionUtil;

    @Inject
    CrudPropertyFilters filters;

    @Inject
    ComponentUtil componentUtil;

    @SuppressWarnings("unused")
    private static class DefaultCrudBrowserView extends
            DefaultCrudViewComponent<DefaultCrudListController> {

        @MembersLabeled
        enum Labels {
            FILTER, ACTIONS
        }

        @Inject
        LabelUtil labelUtil;

        /**
         * @return
         */
        @Override
        protected Component createComponents() {

            // create columns
            ArrayList<Column<Object>> columns = new ArrayList<>();
            for (PersistentProperty p : controller.columnProperties) {
                PropertyInfo property = p.getProperty();
                columns.add(new Column<>(() -> new CDataGrid.Cell(labelUtil
                        .getPropertyLabel(property)), item -> new Cell(
                        new CText(Objects.toString(property.getValue(item)))))
                        .TEST_NAME(property.getName()));
            }
            if (controller.getItemActionsFactory() != null)
                columns.add(new Column<Object>(() -> new Cell(new CText(
                        label(Labels.ACTIONS))), controller
                        .getItemActionsFactory()).TEST_NAME("actions"));

            // @formatter:off
            return toComponent(html -> html
                    .div().CLASS("panel panel-default")
                        .div().CLASS("panel-heading").content(Labels.FILTER)
                        .div().CLASS("panel-body")
                            .fForEach(controller.filterList, filter -> {
                                html.add(filter.getComponent());
                            })
                        ._div()
                    ._div()
                    .add(new CButton(controller, x -> x.search())
                        .apply(CButtonTemplate.setArgs(x -> x.primary())))
                    .add(new CDataGrid<Object>().TEST_NAME("resultList")
                            .setColumns(columns)
                            .bindOneWay(
                                    g -> g.setItems(controller.data()
                                            .getItems()))).div()
                    .TEST_NAME("bottom-actions")
                      .fIf(controller.bottomActions!=null, ()->html
                          .add(controller.bottomActions))
                    ._div());
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

    public DefaultCrudListController setBottomActions(Component bottomActions) {
        this.bottomActions = bottomActions;
        return this;
    }

    public PersistentType type;

    BindingGroup<Data> data = new BindingGroup<>(new Data());

    List<PersistentProperty> columnProperties;

    List<CrudPropertyFilter> filterList;

    Data data() {
        return data.proxy();
    }

    private EntityManager getEm() {
        return emh.getEntityManager(type.getEmQualifier());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Labeled
    @GlyphiconIcon(Glyphicon.search)
    public void search() {
        EntityManager em = getEm();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery q = cb.createQuery(type.getEntityClass());
        Root root = q.from(type.getEntityClass());
        q.select(root);

        ArrayList<Predicate> whereClauses = new ArrayList<>();
        for (CrudPropertyFilter filter : filterList) {
            filter.applyFilter(new CrudFilterPersitenceContext() {

                @Override
                public CriteriaQuery<?> query() {
                    return q;
                }

                @Override
                public CriteriaBuilder cb() {
                    return cb;
                }

                @Override
                public Root<?> root() {
                    return root;
                }

                @Override
                public void addWhere(Predicate predicate) {
                    whereClauses.add(predicate);
                }
            });
        }

        q.where(whereClauses.toArray(new Predicate[] {}));

        data.get().setItems(em.createQuery(q).getResultList());
        data.pullUp();
    }

    public DefaultCrudListController initialize(Class<?> entityClass,
            Class<? extends Annotation> emQualifier) {
        Preconditions.checkNotNull(entityClass, "entityClass is null");
        type = crudReflectionUtil.getPersistentType(emQualifier, entityClass);

        columnProperties = crudReflectionUtil.getBrowserProperties(type);
        filterList = columnProperties.stream().map(filters::getFactory)
                .collect(toList());

        search();

        return this;
    }

    public Function<Object, Cell> getItemActionsFactory() {
        return itemActionsFactory;
    }

    public DefaultCrudListController setItemActionsFactory(
            Function<Object, Cell> itemActionsFactory) {
        this.itemActionsFactory = itemActionsFactory;
        return this;
    }

}