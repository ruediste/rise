package com.github.ruediste.rise.component.tree;

import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.DelegatingHtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlCanvasTarget;
import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducerHtmlCanvasTarget;
import com.github.ruediste.rise.api.SubControllerComponent;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.BindingInfo;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.util.Pair;
import com.github.ruediste.rise.util.Try;
import com.github.ruediste1.i18n.label.LabelUtil;

public class FragmentCanvasTarget extends DelegatingHtmlCanvasTarget {

    @Inject
    public ComponentUtil util;

    @Inject
    public LabelUtil labelUtil;

    private HtmlProducerHtmlCanvasTarget delegate = new HtmlProducerHtmlCanvasTarget();
    private GroupHtmlFragment parentFragment;

    private SubControllerComponent controller;

    {
        initializeParentFragment();
    }

    private void initializeParentFragment() {
        parentFragment = new GroupHtmlFragment() {
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

    public Component toFragment(Runnable renderer) {
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

    public void addProducer(HtmlProducer producer, boolean commitAttributes) {
        delegate.addProducer(producer, commitAttributes);
    }

    public List<HtmlProducer> getProducers() {
        return delegate.getProducers();
    }

    /**
     * Render the given fragment.
     */
    public void render(Component fragment) {
        delegate.addProducer(fragment.getHtmlProducer(), true);
    }

    public ComponentUtil util() {
        return util;
    }

    public void direct(Runnable runnable) {
        addProducer(cosumer -> {
            Component fragment = toFragment(runnable);
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

    public <T> ValueHandle<T> createValueHandle(Supplier<T> accessor, boolean isLabelProperty) {
        Try<BindingInfo<T>> infoTry = BindingUtil.tryExtractBindingInfo(accessor);
        if (infoTry.isPresent()) {
            BindingInfo<T> info = infoTry.get();
            if (isLabelProperty && info.modelProperty != null) {
                getLabelUtil().property(info.modelProperty).tryLabel()
                        .ifPresent(label -> getParentFragment().addLabel(label));
            }
            getParentFragment().getBindingInfos().add(Pair.of(controller, info));
            return new ValueHandle<T>() {

                @Override
                public T get() {
                    return accessor.get();
                }

                @Override
                public void set(T value) {
                    info.setModelProperty(value);
                }
            };

        } else {
            return new ValueHandle<T>() {

                @Override
                public T get() {
                    return accessor.get();
                }

                @Override
                public void set(T value) {
                    throw new UnsupportedOperationException("unable to set value, could not parse accessor",
                            infoTry.getFailure());
                }
            };
        }
    }

}
