package com.github.ruediste.rise.crud;

import javax.inject.Inject;

import com.github.ruediste.rise.api.ControllerComponent;
import com.github.ruediste.rise.component.binding.BindingGroup;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.crud.CrudUtil.BrowserFactory;
import com.github.ruediste.rise.crud.CrudUtil.BrowserSettings;

public abstract class CrudControllerBase extends ControllerComponent {

    @Inject
    CrudUtil crudUtil;

    public static class Data {
        private Object subController;

        public Object getSubController() {
            return subController;
        }

        public void setSubController(Object subController) {
            this.subController = subController;
        }
    }

    private BindingGroup<Data> data = new BindingGroup<CrudControllerBase.Data>(
            new Data());

    public Data data() {
        return data.proxy();
    }

    public ActionResult browse(Class<?> entityClass) {
        data.get().setSubController(
                crudUtil.getFactory(BrowserFactory.class, entityClass)
                        .createBrowser(entityClass, new BrowserSettings<>()));
        return null;
    }
}
