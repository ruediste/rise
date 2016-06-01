package com.github.ruediste.rise.component.fragment;

import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.DelegatingHtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducerHtmlCanvasTarget;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste1.i18n.label.LabelUtil;

public class FragmentCanvasTarget extends DelegatingHtmlCanvasTarget {

    @Inject
    public ComponentUtil util;

    @Inject
    private LabelUtil labelUtil;

    private HtmlProducerHtmlCanvasTarget delegate = new HtmlProducerHtmlCanvasTarget();
    private GroupHtmlFragment parentFragment;

    private SubControllerComponent controller;

    {
        initializeParentFragment(null);
    }

    private void initializeParentFragment(HtmlFragment parent) {
        parentFragment = new GroupHtmlFragment(parent) {
            HtmlProducerHtmlCanvasTarget delegateCopy = delegate;

            @Override
            protected void produceHtml(HtmlConsumer consumer) {
                delegateCopy.getProducers().forEach(x -> x.produce(consumer));
            }
        };
    }

    @Override
    protected HtmlCanvasTarget getDelegate() {
        return delegate;
    }

    public HtmlFragment toFragment(Runnable renderer) {
        return toFragment(renderer, parentFragment);
    }

    public HtmlFragment toFragment(Runnable renderer, HtmlFragment parent) {
        // prepare current delegate
        delegate.commitAttributes();
        delegate.flush();

        GroupHtmlFragment oldFragment = parentFragment;
        HtmlProducerHtmlCanvasTarget oldDelegate = delegate;
        try {
            // set context
            delegate = new HtmlProducerHtmlCanvasTarget();
            initializeParentFragment(parent);

            // run renderer
            renderer.run();

            // collect producers
            delegate.commitAttributes();
            delegate.flush();

            return parentFragment;
        } finally {
            // reset context
            delegate = oldDelegate;
            parentFragment = oldFragment;
        }
    }

    public GroupHtmlFragment getParentFragment() {
        return parentFragment;
    }

    public void addProducer(HtmlProducer producer, boolean commitAttributes) {
        delegate.addProducer(producer, commitAttributes);
    }

    public List<HtmlProducer> getProducers() {
        return delegate.getProducers();
    }

    /**
     * Add the fragment to the current parent fragment of the canvas. This does
     * not cause the {@link HtmlFragment#getHtmlProducer()} to be added to to
     * the producer list
     */
    public void addFragment(HtmlFragment fragment) {
        fragment.setParent(parentFragment);
        parentFragment.childAdded(fragment);
    }

    public void addFragmentAndRender(HtmlFragment fragment) {
        addFragment(fragment);
        render(fragment);
    }

    /**
     * Render the given fragment.
     */
    public void render(HtmlFragment fragment) {
        if (fragment.getParent() != parentFragment)
            throw new RuntimeException("parent fragment does not match");
        delegate.addProducer(fragment.getHtmlProducer(), true);
    }

    public ComponentUtil util() {
        return util;
    }

    public void direct(Runnable runnable) {
        addProducer(cosumer -> {
            HtmlFragment fragment = toFragment(runnable, null);
            HtmlFragmentUtil.updateStructure(fragment);
            fragment.getHtmlProducer().produce(cosumer);
        } , true);
    }

    public void setController(SubControllerComponent controller) {
        this.controller = controller;
    }

    public SubControllerComponent getController() {
        return controller;
    }

    public LabelUtil getLabelUtil() {
        return labelUtil;
    }
}
