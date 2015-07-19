package com.github.ruediste.rise.crud;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CButton;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CDataGrid.Cell;
import com.github.ruediste.rise.component.components.CDataGrid.Column;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.crud.CrudPropertyFilter.CrudFilterPersitenceContext;
import com.github.ruediste.rise.crud.CrudUtil.BrowserSettings;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.github.ruediste1.i18n.label.Labeled;

public class DefaultCrudBrowserController<T> {
    @Inject
    CrudReflectionUtil crudReflectionUtil;

    @Inject
    CrudPropertyFilters filters;

    @SuppressWarnings("unused")
    private static class DefaultCrudBrowserView<T> extends
            DefaultCrudViewComponent<DefaultCrudBrowserController<T>> {

        @Inject
        LabelUtil labelUtil;

        @Override
        protected Component createComponents() {

            // create columns
            ArrayList<Column<T>> columns = new ArrayList<>();
            for (PropertyDeclaration p : controller.properties) {
                columns.add(new Column<>(() -> new CDataGrid.Cell(labelUtil
                        .getPropertyLabel(p)), item -> new Cell(new CText(
                        Objects.toString(p.getValue(item))))));
            }

            return toComponent(html -> html
                    .h1()
                    .content("Browser for " + controller.entityClass)
                    .div()
                    .fForEach(controller.filterList, filter -> {
                        html.add(filter.getComponent());
                    })
                    .add(new CButton(controller, x -> x.search()))
                    ._div()
                    .add(new CDataGrid<T>().setColumns(columns).bindOneWay(
                            g -> g.setItems(controller.data().getItems()))));
        }
    }

    @Inject
    EntityManagerHolder emh;

    private Class<T> entityClass;
    private BrowserSettings<?> settings;

    static class Data<T> {
        private List<T> items;

        public List<T> getItems() {
            return items;
        }

        public void setItems(List<T> objects) {
            this.items = objects;
        }
    }

    BindingGroup<Data<T>> data = new BindingGroup<>(new Data<>());

    Data<T> data() {
        return data.proxy();
    }

    private EntityManager getEm() {
        return emh.getEntityManager(settings.emQualifier);
    }

    List<PropertyDeclaration> properties;

    private List<CrudPropertyFilter> filterList;

    @Labeled
    @GlyphiconIcon(Glyphicon.search)
    void search() {
        EntityManager em = getEm();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);
        Root<T> root = q.from(entityClass);
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

    public DefaultCrudBrowserController<T> initialize(Class<T> entityClass,
            BrowserSettings<T> settings) {
        this.entityClass = entityClass;
        this.settings = settings;

        properties = crudReflectionUtil.getBrowserProperties(entityClass);
        filterList = properties.stream().map(filters::createPropertyFilter)
                .collect(toList());

        search();

        return this;
    }
}