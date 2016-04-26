package com.github.ruediste.rise.component.fragment;

import java.util.List;

import com.github.ruediste.rendersnakeXT.canvas.DelegatingHtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducerHtmlCanvasTarget;

public class FragmentCanvasTarget extends DelegatingHtmlCanvasTarget {

    private HtmlProducerHtmlCanvasTarget delegate = new HtmlProducerHtmlCanvasTarget();
    private GroupHtmlFragment parentFragment;

    {
        initializeParentFragment();
    }

    private void initializeParentFragment() {
        parentFragment = new GroupHtmlFragment(null) {
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

    public HtmlFragment toFragment(HtmlFragment parent, Runnable renderer) {
        // prepare current delegate
        delegate.commitAttributes();
        delegate.flush();

        GroupHtmlFragment oldFragment = parentFragment;
        HtmlProducerHtmlCanvasTarget oldDelegate = delegate;
        try {
            // set context
            delegate = new HtmlProducerHtmlCanvasTarget();
            initializeParentFragment();

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

    public void addProducer(HtmlProducer producer) {
        delegate.addProducer(producer);
    }

    public List<HtmlProducer> getProducers() {
        return delegate.getProducers();
    }

}
