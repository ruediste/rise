package com.github.ruediste.rise.core.navigation;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.CoreUtil;
import com.github.ruediste.rise.core.navigation.Navigation.NavigationItem;

public class NavigationBuilder {

    @Inject
    CoreUtil util;

    private final Navigation result = new Navigation();

    private Deque<NavigationItem> currentGroup = new ArrayDeque<>();

    private NavigationBuilder add(NavigationItem item) {
        if (currentGroup.isEmpty())
            result.getRootItems().add(item);
        else
            currentGroup.peek().children.add(item);
        return this;
    }

    public NavigationBuilder add(String text, ActionResult target) {
        Method invokedMethod = util.toActionInvocation(target).methodInvocation
                .getMethod();
        return add(new NavigationItem(text, Optional.of(target),
                x -> x.methodInvocation.getMethod().equals(invokedMethod)));
    }

    public NavigationBuilder group(String text) {
        NavigationItem group = new NavigationItem(text, Optional.empty(),
                x -> false);
        add(group);
        currentGroup.push(group);
        return this;
    }

    public NavigationBuilder _group() {
        currentGroup.pop();
        return this;
    }

    public Navigation getResult() {
        return result;
    }
}
