package com.github.ruediste.rise.sample.welcome;

import com.github.ruediste.rise.sample.PageView;
import com.github.ruediste.rise.sample.SampleCanvas;
import com.github.ruediste.rise.sample.admin.ProtectedController;
import com.github.ruediste.rise.sample.component.SampleComponentController;
import com.github.ruediste.rise.sample.crud.CrudController;
import com.github.ruediste.rise.sample.db.TodoController;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;

@Labeled
@Label("Welcome")
public class WelcomeView extends PageView<WelcomeController, WelcomeController.Data> {

    @Override
    public void renderContent(SampleCanvas html) {
        html.h1().content(label(this));
        html.bButtonA().HREF(go(TodoController.class).index()).content("Todo Items");
        html.bButtonA().HREF(go().error()).content("Page with error");
        html.rButtonA(go(CrudController.class).showTodos());
        html.rButtonA(go(CrudController.class).showTodoCategories());
        html.rButtonA(go(SampleComponentController.class).index());
        html.rButtonA(go(ProtectedController.class).protectedUserPage(), a -> a.nonAuthorizedNormal());
        html.rButtonA(go(ProtectedController.class).protectedUserPage(), a -> a.nonAuthorizedHidden());
        html.rButtonA(go(ProtectedController.class).protectedAdminPage());
    }
}
