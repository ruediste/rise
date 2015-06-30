package com.github.ruediste.rise.sample;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.navigation.Navigation;
import com.github.ruediste.rise.core.navigation.NavigationsContainer;
import com.github.ruediste.rise.sample.welcome.WelcomeController;
import com.github.ruediste1.i18n.label.MembersLabeled;

@Singleton
public class Navigations extends NavigationsContainer {

    @MembersLabeled
    private enum GroupLabels {
        A
    }

    Navigation topNavigation;

    @Override
    protected void initializeImpl() {
        topNavigation = build().group(GroupLabels.A)
                .add(go(WelcomeController.class).index())._group()
                .add(go(WelcomeController.class).other()).getResult();
    }
}
