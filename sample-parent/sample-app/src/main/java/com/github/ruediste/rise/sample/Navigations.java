package com.github.ruediste.rise.sample;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.navigation.Navigation;
import com.github.ruediste.rise.core.navigation.NavigationsContainer;
import com.github.ruediste.rise.sample.component.SampleComponentController;
import com.github.ruediste.rise.sample.crud.CrudController;
import com.github.ruediste.rise.sample.welcome.WelcomeController;
import com.github.ruediste1.i18n.label.MembersLabeled;

/**
 * Contains the {@link Navigation} of the sample application, used to render the
 * menu.
 */
@Singleton
public class Navigations extends NavigationsContainer {

    @MembersLabeled
    private enum GroupLabels {
        CRUD
    }

    Navigation topNavigation;

    @Override
    protected void initializeImpl() {
        topNavigation = build().add(go(WelcomeController.class).index())
                .add(go(SampleComponentController.class).index()).group(GroupLabels.CRUD)
                .add(go(CrudController.class).showTodos()).add(go(CrudController.class).showTodoCategories())
                .add(go(CrudController.class).browse(User.class, null), "Users")._group().getResult();
    }
}
