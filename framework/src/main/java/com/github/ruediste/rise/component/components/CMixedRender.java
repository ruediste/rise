package com.github.ruediste.rise.component.components;

import java.util.ArrayList;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

/**
 * Component containing a mix of fixed html snippets and child components.
 */
@DefaultTemplate(CMixedRenderTemplate.class)
public class CMixedRender extends MultiChildrenComponent<CMixedRender> {

    private static abstract class Entry {

        public abstract void doRender(RiseCanvas<?> html, ComponentUtil util);

    }

    private static class ComponentEntry extends Entry {

        private Component child;

        public ComponentEntry(Component child) {
            this.child = child;
        }

        @Override
        public void doRender(RiseCanvas<?> html, ComponentUtil util) {
            html.render(util.component(child));
        }
    }

    private static class BufferEntry extends Entry {

        private byte[] buffer;

        public BufferEntry(byte[] buffer) {
            this.buffer = buffer;
        }

        @Override
        public void doRender(RiseCanvas<?> html, ComponentUtil util) {
            html.writeRaw(buffer);
        }

    }

    private ArrayList<Entry> entries = new ArrayList<>();

    @Override
    public CMixedRender add(Component child) {
        entries.add(new ComponentEntry(child));
        return super.add(child);
    }

    public CMixedRender add(byte[] buffer) {
        entries.add(new BufferEntry(buffer));
        return this;
    }

    public void doRender(RiseCanvas<?> html, ComponentUtil util) {
        for (Entry entry : entries) {
            entry.doRender(html, util);
        }
    }
}
