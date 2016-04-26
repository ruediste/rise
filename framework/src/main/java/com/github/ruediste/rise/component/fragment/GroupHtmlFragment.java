package com.github.ruediste.rise.component.fragment;

import java.util.LinkedHashSet;

import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;

public abstract class GroupHtmlFragment extends HtmlFragment {

    LinkedHashSet<HtmlFragment> children = new LinkedHashSet<>();

    public GroupHtmlFragment(HtmlFragment parent) {
        super(parent);
    }

    @Override
    public Iterable<HtmlFragment> getChildren() {
        return children;
    }

    @Override
    public void childAdded(HtmlFragment child) {
        children.add(child);
    }

    @Override
    public void childRemoved(HtmlFragment child) {
        children.remove(child);
    }

    @Override
    protected void produceHtml(HtmlConsumer consumer) {
        children.forEach(x -> x.render(consumer));
    }
}
