package com.github.ruediste.rise.component.components;

import java.util.ArrayList;

import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.integration.RiseCanvas;

@DefaultTemplate(CRenderTemplate.class)
public class CRender extends MultiChildrenComponent<CRender> {

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
    public CRender add(Component child) {
        entries.add(new ComponentEntry(child));
        return super.add(child);
    }

    public CRender add(byte[] buffer) {
        entries.add(new BufferEntry(buffer));
        return this;
    }

    public void doRender(RiseCanvas<?> html, ComponentUtil util) {
        for (Entry entry : entries) {
            entry.doRender(html, util);
        }
    }
}
