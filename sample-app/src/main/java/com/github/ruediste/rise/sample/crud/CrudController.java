package com.github.ruediste.rise.sample.crud;

import com.github.ruediste.rise.component.components.CComponentStack;
import com.github.ruediste.rise.component.components.CPage;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.crud.CrudControllerBase;
import com.github.ruediste.rise.sample.ViewComponent;
import com.github.ruediste.rise.sample.db.TodoCategory;
import com.github.ruediste.rise.sample.db.TodoItem;
import com.github.ruediste1.i18n.label.Labeled;

/**
 * Controller exposing the CRUD UI.
 */
public class CrudController extends CrudControllerBase {

    @Labeled
    public static class View extends ViewComponent<CrudController> {

        @Override
        protected Component createComponents() {
            return new CPage(label(this))
                    .add(new CComponentStack(toSubView(() -> controller.data(),
                            x -> x.getSubController())));
        }
    }

    @Labeled
    public ActionResult showTodos() {
        return browse(TodoItem.class, null);
    }

    @Labeled
    public ActionResult showTodoCategories() {
        return browse(TodoCategory.class, null);
    }
}
