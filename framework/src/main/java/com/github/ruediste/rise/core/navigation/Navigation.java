package com.github.ruediste.rise.core.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;

public class Navigation {

    public static class NavigationItem {
        public final ArrayList<NavigationItem> children = new ArrayList<>();

        public final Optional<ActionResult> target;
        public final String text;
        public final Predicate<ActionInvocation<Object>> selected;

        public NavigationItem(String text, Optional<ActionResult> target,
                Predicate<ActionInvocation<Object>> selected) {
            this.text = text;
            this.target = target;
            this.selected = selected;
        }

        public List<NavigationItem> getChildren() {
            return children;
        }
    }

    private final ArrayList<NavigationItem> rootItems = new ArrayList<>();

    public ArrayList<NavigationItem> getRootItems() {
        return rootItems;
    }
}
