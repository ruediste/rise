package com.github.ruediste.rise.core.navigation;

import java.util.HashMap;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste.rise.core.navigation.Navigation.NavigationItem;

public class NavigationItemSelectionCache {

    @Inject
    CoreRequestInfo info;
    private HashMap<NavigationItem, Boolean> cache = new HashMap<>();

    public boolean isSelected(NavigationItem item) {
        return isSelected(item, info.getObjectActionInvocation());
    }

    public boolean isSelected(NavigationItem item, ActionInvocation<Object> currentActionInvocation) {
        return cache.computeIfAbsent(item, x -> {
            if (x.selected.test(currentActionInvocation))
                return true;
            for (NavigationItem child : item.getChildren()) {
                if (isSelected(child, currentActionInvocation))
                    return true;
            }
            return false;
        });
    }
}
