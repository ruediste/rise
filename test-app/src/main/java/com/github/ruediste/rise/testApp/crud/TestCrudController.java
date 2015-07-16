package com.github.ruediste.rise.testApp.crud;

import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.crud.CrudControllerBase;
import com.github.ruediste.rise.testApp.component.ViewComponent;
import com.github.ruediste1.i18n.label.Labeled;

public class TestCrudController extends CrudControllerBase {

    @Labeled
    public static class View extends ViewComponent<TestCrudController> {

        @Override
        protected Component createComponents() {
            return new CPage(label(this)).add(toSubView(
                    () -> controller.data(), x -> x.getSubController()));
        }
    }

}
