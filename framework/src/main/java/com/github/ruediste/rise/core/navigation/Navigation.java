package com.github.ruediste.rise.core.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.core.ActionResult;
import com.github.ruediste.rise.core.actionInvocation.ActionInvocation;
import com.github.ruediste1.i18n.lString.LString;

/**
 * Represents a hierarchical navigation structure. Used to render menus
 */
public class Navigation {

    /**
     * Item of a {@link Navigation}
     */
    public static class NavigationItem {
        public final ArrayList<NavigationItem> children = new ArrayList<>();

        /**
         * Target of the navigation. For inner nodes (sub menus), this is
         * typically empty.
         */
        public final Optional<ActionResult> target;

        /**
         * Text to display
         */
        public final LString text;

        /**
         * Icon
         */
        public Optional<Renderable<Html5Canvas<?>>> icon;

        /**
         * Predicate to test if this navigation is selected when the given
         * action method is requested.
         */
        public final Predicate<ActionInvocation<Object>> selected;

        public NavigationItem(LString text,
                Optional<Renderable<Html5Canvas<?>>> icon,
                Optional<ActionResult> target,
                Predicate<ActionInvocation<Object>> selected) {
            this.text = text;
            this.icon = icon;
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
