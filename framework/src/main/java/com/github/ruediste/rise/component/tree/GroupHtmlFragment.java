package com.github.ruediste.rise.component.tree;

import java.util.LinkedHashSet;

import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;

public abstract class GroupHtmlFragment extends Component {

    LinkedHashSet<Component> children = new LinkedHashSet<>();

    public GroupHtmlFragment() {
    }

    public GroupHtmlFragment(Component parent) {
        super(parent);
    }

    @Override
    public Iterable<Component> getChildren() {
        return children;
    }

    @Override
    public void childAdded(Component child) {
        children.add(child);
    }

    @Override
    public void childRemoved(Component child) {
        children.remove(child);
    }

    @Override
    protected void produceHtml(HtmlConsumer consumer) {
        children.forEach(x -> x.render(consumer));
    }
}
