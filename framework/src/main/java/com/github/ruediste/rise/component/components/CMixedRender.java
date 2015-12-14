package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.google.common.base.Preconditions;

/**
 * Component containing a mix of fixed html snippets and child components.
 */
@DefaultTemplate(CMixedRenderTemplate.class)
public class CMixedRender extends ComponentBase<CMixedRender> {

    public static abstract class Entry {

        public abstract void doRender(RiseCanvas<?> html, ComponentUtil util);

        public Collection<Component> getChildren() {
            return Collections.emptyList();
        }
    }

    private ArrayList<Entry> entries = new ArrayList<>();

    public CMixedRender add(Component child) {
        Preconditions.checkNotNull(child);

        entries.add(new Entry() {

            @Override
            public Collection<Component> getChildren() {
                return Collections.singleton(child);
            }

            @Override
            public void doRender(RiseCanvas<?> html, ComponentUtil util) {
                html.render(util.component(child));
            }
        });
        if (child.getParent() != null)
            child.getParent().childRemoved(child);
        child.parentChanged(this);
        return this;
    }

    public CMixedRender add(Entry entry) {
        entries.add(entry);
        return this;
    }

    public CMixedRender add(byte[] buffer) {
        entries.add(new Entry() {

            @Override
            public void doRender(RiseCanvas<?> html, ComponentUtil util) {
                html.writeRaw(buffer);
            }
        });
        return this;
    }

    public void doRender(RiseCanvas<?> html, ComponentUtil util) {
        for (Entry entry : entries) {
            entry.doRender(html, util);
        }
    }

    @Override
    public Iterable<Component> getChildren() {
        return entries.stream().flatMap(e -> e.getChildren().stream())
                .collect(toList());
    }

    @Override
    public void childRemoved(Component child) {
        throw new UnsupportedOperationException();
    }
}
