package com.github.ruediste.rise.component.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rise.api.ApiJumpPad;
import com.github.ruediste.rise.component.ComponentUtil;
import com.github.ruediste.rise.component.binding.Binding;
import com.github.ruediste.rise.component.binding.BindingUtil;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste1.i18n.lString.LString;

public interface FragmentCanvas<TSelf extends FragmentCanvas<TSelf>> extends Html5Canvas<TSelf> {

    @Override
    FragmentCanvasTarget internal_target();

    default TSelf render(HtmlProducer producer, boolean commitAttributes) {
        internal_target().addProducer(producer, commitAttributes);
        return self();
    }

    /**
     * Add a fragment to the current parent fragment and render the producers of
     * the fragment
     */
    default TSelf addFragmentAndRender(HtmlFragment fragment) {
        internal_target().addFragmentAndRender(fragment);
        return self();
    }

    default TSelf render(HtmlFragment fragment) {
        internal_target().render(fragment);
        return self();
    }

    /**
     * Execute the given runnable whenever the page is rendered.
     */
    default TSelf direct(Runnable runnable) {
        internal_target().direct(runnable);
        return self();
    }

    /**
     * create a new fragment with the current parent of the canvas as parent
     */
    default TSelf toFragmentAndRender(Runnable renderer) {
        HtmlFragment fragment = internal_target().toFragment(renderer);
        return render(fragment);
    }

    /**
     * create a new fragment with the current parent of the canvas as parent
     */
    default HtmlFragment toFragment(Runnable renderer) {
        return internal_target().toFragment(renderer);
    }

    /**
     * Create a fragment from the given renderer and set the specified parent
     * fragment
     * 
     * @param parent
     *            parent fragment to add the created fragment to, can be null
     */
    default HtmlFragment toFragment(Runnable renderer, HtmlFragment parent) {
        return internal_target().toFragment(renderer, parent);
    }

    /**
     * Render ifTru, depending on the condition.
     * 
     * the fragment is always present in the html structure.
     */
    default TSelf fIf(Supplier<Boolean> condition, Runnable ifTrue) {
        return fIf(condition, ifTrue, () -> {
        });
    }

    /**
     * Render ifTrue of ifFalse, depending on the condition.
     * 
     * Both cases are always present in the html structure.
     */
    default TSelf fIf(Supplier<Boolean> condition, Runnable ifTrue, Runnable ifFalse) {
        return addFragmentAndRender(new HtmlFragment() {
            boolean currentState;
            HtmlFragment trueFragment = toFragment(ifTrue, this);
            HtmlFragment falseFragment = toFragment(ifFalse, this);

            @Override
            public Iterable<HtmlFragment> getChildren() {
                return Arrays.asList(trueFragment, falseFragment);
            }

            @Override
            public void updateStructure(UpdateStructureArg arg) {
                currentState = condition.get();
            }

            @Override
            protected void produceHtml(HtmlConsumer consumer) {
                if (currentState)
                    trueFragment.render(consumer);
                else
                    falseFragment.render(consumer);
            }
        });

    }

    /**
     * Render ifTrue of ifFalse, depending on the condition.
     * 
     * Only the fragment for the current condition (true or false) is present in
     * the structure. Whenever the structure changes, the fragments are
     * recreated.
     */
    default TSelf fIfT(Supplier<Boolean> condition, Runnable ifTrue, Runnable ifFalse) {
        return addFragmentAndRender(new HtmlFragment() {
            HtmlFragment currentFragment;
            boolean currentState;

            @Override
            public Iterable<HtmlFragment> getChildren() {
                if (currentFragment == null)
                    return Collections.emptyList();
                return Arrays.asList(currentFragment);
            }

            @Override
            protected void produceHtml(HtmlConsumer consumer) {
                if (currentFragment != null)
                    currentFragment.render(consumer);
            }

            @Override
            public void updateStructure(UpdateStructureArg arg) {
                Boolean newState = condition.get();
                if (newState != currentState) {
                    currentState = newState;
                    currentFragment = currentState ? toFragment(ifTrue, this) : toFragment(ifFalse, this);
                    arg.structureUpdated();
                }
            }

        });
    }

    default <T> TSelf fForEach(Iterable<T> items, Consumer<T> fragmentFactory) {
        return fForEach(() -> items, fragmentFactory);
    }

    default <T> TSelf fForEach(Supplier<Iterable<T>> items, Consumer<T> fragmentFactory) {
        return addFragmentAndRender(new HtmlFragment() {
            Map<T, HtmlFragment> fragments = new HashMap<>();
            ArrayList<HtmlFragment> fragmentList = new ArrayList<>();

            @Override
            protected void produceHtml(HtmlConsumer consumer) {
                fragmentList.forEach(x -> x.render(consumer));
            }

            @Override
            public Iterable<HtmlFragment> getChildren() {
                return fragmentList;
            }

            @Override
            public void updateStructure(UpdateStructureArg arg) {
                Map<T, HtmlFragment> newFragments = new HashMap<>();
                fragmentList.clear();
                for (T item : items.get()) {
                    HtmlFragment fragment = fragments.remove(item);
                    if (fragment == null) {
                        fragment = toFragment(() -> fragmentFactory.accept(item), this);
                        arg.structureUpdated();
                    }
                    newFragments.put(item, fragment);
                    fragmentList.add(fragment);
                }

                // if any fragment remains, it will be removed, thus the
                // structure changed
                if (!fragments.isEmpty())
                    arg.structureUpdated();
                fragments.values().forEach(f -> f.setParent(null));

                fragments = newFragments;
            }
        });
    }

    default TSelf RonClick(Runnable handler) {
        ComponentUtil util = internal_target().util();
        HtmlFragment fragment = new HtmlFragment(internal_target().getParentFragment()) {
            @Override
            public void processActions() {
                if (util.isParameterDefined(this, "clicked"))
                    handler.run();
            }

            @Override
            public String toString() {
                return "onClick-clicked";
            }
        };
        return DATA("rise-on-click", util.getParameterKey(fragment, "clicked")).render(fragment);
    }

    default TSelf VALUE(@Capture Supplier<String> value) {
        return VALUE(value, true);
    }

    /**
     * Add a value attribute to an input object. If the provided supplier
     * accesses a controller property, a binding to this controller property is
     * set up, with an intermediate value object as view state.
     * 
     * <p>
     * Otherwise the value is read directly from the view state for each render
     * pass.
     */
    default TSelf VALUE(@Capture Supplier<String> value, boolean isLabelProperty) {
        BindingUtil.tryExtractBindingInfo(value).ifPresent(info -> {
            if (isLabelProperty && info.modelProperty != null) {
                internal_target().getLabelUtil().property(info.modelProperty).tryLabel()
                        .ifPresent(label -> fMarkLabel(label));
            }
            if (info.accessesController) {
                // bind to the controller
                ValueHandleImpl<String> viewVal = new ValueHandleImpl<>();
                Binding binding = info.createBinding(viewVal);
                ApiJumpPad.registerBinding(internal_target().getController(), binding);
                internal_target().getParentFragment().getBindings().add(binding);
                binding.pullUp();
                VALUE(viewVal);
            } else {
                addAttribute("value", value);
            }
        }).ifFailure(() -> addAttribute("value", value));
        return self();
    }

    default TSelf VALUE(ValueHandle<String> value) {
        ComponentUtil util = internal_target().util();
        HtmlFragment fragment = new HtmlFragment(internal_target().getParentFragment()) {
            @Override
            public void applyValues() {
                util.getParameterValue(this, "value").ifPresent(value::set);
            }

            @Override
            public String toString() {
                return "value";
            }
        };
        return addAttribute("value", value).NAME(util.getParameterKey(fragment, "value")).render(fragment);
    }

    /**
     * Add a label to the current parent fragment Can be retrieved afterwards
     * using {@link HtmlFragment#getLabels()} on any of the parent labels.
     */
    default TSelf fMarkLabel(LString label) {
        internal_target().getParentFragment().addLabel(label);
        return self();
    }
}
