package com.github.ruediste.rise.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.component.components.CDataGrid;
import com.github.ruediste.rise.component.components.CDataGrid.Cell;
import com.github.ruediste.rise.component.components.CDataGrid.Column;
import com.github.ruediste.rise.component.components.CText;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
import com.github.ruediste.rise.crud.CrudUtil.BrowserSettings;
import com.github.ruediste1.i18n.label.LabelUtil;

public class DefaultCrudBrowserController<T> {
    private static class DefaultCrudBrowserView<T> extends
            DefaultCrudViewComponent<DefaultCrudBrowserController<T>> {

        @Inject
        CrudReflectionUtil crudReflectionUtil;

        @Inject
        LabelUtil labelUtil;

        @Override
        protected Component createComponents() {

            ArrayList<Column<T>> columns = new ArrayList<>();
            for (PropertyInfo p : crudReflectionUtil
                    .getListProperties(controller.entityClass)) {
                columns.add(new Column<>(() -> new CDataGrid.Cell(labelUtil
                        .getPropertyLabel(p)), item -> new Cell(new CText(
                        Objects.toString(item)))));
            }

            return toComponent(html -> html
                    .h1()
                    .content("Browser for " + controller.entityClass)
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

    public DefaultCrudBrowserController<T> initialize(Class<T> entityClass,
            BrowserSettings<T> settings) {
        this.entityClass = entityClass;
        this.settings = settings;

        EntityManager em = getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(entityClass);
        Root<T> root = q.from(entityClass);
        q.select(root);
        data.get().setItems(em.createQuery(q).getResultList());

        return this;
    }
}