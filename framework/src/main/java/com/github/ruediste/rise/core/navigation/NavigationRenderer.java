package com.github.ruediste.rise.core.navigation;

import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.ruediste.rendersnakeXT.canvas.BootstrapCanvas;
import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.core.navigation.Navigation.NavigationItem;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class NavigationRenderer {

    @Inject
    Provider<NavigationItemSelectionCache> cache;

    public static class NavigationRendererOptions {
        private BootstrapCanvas<?> html;

        public NavigationRendererOptions(BootstrapCanvas<?> html) {
            this.html = html;
        }

        public NavigationRendererOptions pills() {
            html.CLASS("nav-pills");
            return this;
        }

        public NavigationRendererOptions tabs() {
            html.CLASS("nav-tabs");
            return this;
        }

        /**
         * Easily make tabs or pills equal widths of their parent at screens
         * wider than 768px with .nav-justified. On smaller screens, the nav
         * links are stacked.
         */
        public NavigationRendererOptions justified() {
            html.CLASS("nav-justified");
            return this;
        }

        /**
         * Pills are also vertically stackable.
         */
        public NavigationRendererOptions stacked() {
            html.CLASS("nav-pills nav-stacked");
            return this;
        }
    }

    private static class Ctx {
        int nextId;
        NavigationItemSelectionCache cache;

        String nextId() {
            return baseName + nextId++;
        }

        String baseName;
        public Consumer<NavigationRendererOptions> opts;
    }

    public Renderable<BootstrapRiseCanvas<?>> nav(Navigation nav,
            String baseName, Consumer<NavigationRendererOptions> opts) {
        return html -> {
            Ctx ctx = new Ctx();
            ctx.baseName = baseName;
            ctx.opts = opts;
            ctx.cache = cache.get();
            renderItems(html, ctx, nav.getRootItems());
        };
    }

    private void renderItems(BootstrapRiseCanvas<?> html, Ctx ctx,
            Iterable<NavigationItem> items) {
        String groupName = ctx.nextId();
        html.div().CLASS("navbar navbar-default");
        html.bContainer_fluid().div().CLASS("collapse navbar-collapse");

        html.ul().CLASS("nav navbar-nav");
        ctx.opts.accept(new NavigationRendererOptions(html));
        for (NavigationItem item : items) {
            String id = ctx.nextId();
            html.li();
            if (ctx.cache.isSelected(item))
                html.CLASS("active");
            if (item.target.isPresent())
                html.a().HREF(item.target.get()).content(item.text);
            else
                html.a().HREF("#").content(item.text);
            html._li();
        }
        html._ul();
        html._div()._bContainer_fluid()._div();

        for (NavigationItem item : items) {
            if (!item.children.isEmpty()) {
                renderItems(html, ctx, item.getChildren());
            }
        }
    }
}
