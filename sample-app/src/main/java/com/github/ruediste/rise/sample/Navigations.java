package com.github.ruediste.rise.sample;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.navigation.Navigation;
import com.github.ruediste.rise.core.navigation.NavigationsContainer;
import com.github.ruediste.rise.sample.welcome.WelcomeController;

@Singleton
public class Navigations extends NavigationsContainer {

    Navigation topNavigation;

    @Override
    protected void initializeImpl() {
        topNavigation = build().group("A")
                .add("Home", go(WelcomeController.class).index())._group()
                .add("Other", go(WelcomeController.class).other()).getResult();
    }
}
