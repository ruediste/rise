package com.github.ruediste.rise.sample;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.navigation.Navigation;
import com.github.ruediste.rise.core.navigation.NavigationsContainer;
import com.github.ruediste.rise.sample.welcome.WelcomeController;

@Singleton
public class Navigations extends NavigationsContainer {

    Navigation sideNavigation;

    @Override
    protected void initializeImpl() {
        sideNavigation = build().group("A")
                .add("Home", go(WelcomeController.class).index())._group()
                .add("Other", go(WelcomeController.class).other()).getResult();
    }
}
