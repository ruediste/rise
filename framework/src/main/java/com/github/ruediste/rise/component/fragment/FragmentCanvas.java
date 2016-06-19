package com.github.ruediste.rise.component.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.ruediste.rendersnakeXT.canvas.Html5Canvas;
import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rise.component.ComponentUtil;
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
     * Add a fragment to the current parent fragment
     */
    default TSelf addFragment(HtmlFragment fragment) {
        fragment.setParent(getParentFragment());
        return self();
    }

    /**
     * Render the given fragment. Does NOT add the fragment as child of the
     * parent fragment of the canvas.
     */
    default TSelf render(HtmlFragment fragment) {
        internal_target().render(fragment);
        return self();
    }

    default TSelf addFragmentAndRender(HtmlFragment fragment) {
        return addFragment(fragment).render(fragment);
    }

    /**
     * Create a new fragment. The fragment is created detached (not a child of
     * any other fragment)
     */
    default HtmlFragment toFragment(Runnable renderer) {
        return internal_target().toFragment(renderer);
    }

    default HtmlFragment toFragmentAndAdd(Runnable renderer) {
        HtmlFragment result = internal_target().toFragment(renderer);
        result.setParent(getParentFragment());
        return result;
    }

    default HtmlFragment toFragment(Runnable renderer, HtmlFragment parent) {
        HtmlFragment result = internal_target().toFragment(renderer);
        result.setParent(parent);
        return result;
    }

    /**
     * Execute the given runnable whenever the page is rendered.
     */
    default TSelf direct(Runnable runnable) {
        internal_target().direct(runnable);
        return self();
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

    default <T> TSelf fIfPresent(Supplier<Optional<T>> optional, Consumer<T> ifPresent) {
        return fIfPresent(optional, ifPresent, () -> {
        });
    }

    default <T> TSelf fIfPresent(Supplier<Optional<T>> optional, Consumer<T> ifPresent, Runnable ifAbsent) {
        return addFragmentAndRender(new HtmlFragment() {
            HtmlFragment currentFragment;
            Optional<T> currentState;

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
                Optional<T> newState = optional.get();
                if (currentState == null || !Objects.equals(newState, currentState)) {
                    currentState = newState;
                    currentFragment = currentState.isPresent()
                            ? toFragment(() -> ifPresent.accept(optional.get().get()), this)
                            : toFragment(ifAbsent, this);
                    arg.structureUpdated();
                }
            }

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
            Boolean currentState;

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
                boolean newState = condition.get();
                if (currentState == null || newState != currentState) {
                    currentState = newState;
                    currentFragment = currentState ? toFragment(ifTrue, this) : toFragment(ifFalse, this);
                    arg.structureUpdated();
                }
            }

        });
    }

    default <T> TSelf fForEach(Supplier<Iterable<T>> items, Consumer<T> fragmentFactory) {
        return fForEach(items, (idx, i) -> fragmentFactory.accept(i));
    }

    /**
     * Apply the fragment for each item in the list. The list is updated when
     * items is updated
     */
    default <T> TSelf fForEach(Supplier<Iterable<T>> items, BiConsumer<Integer, T> fragmentFactory) {
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
                int idx = 0;
                for (T item : items.get()) {
                    HtmlFragment fragment = fragments.remove(item);
                    if (fragment == null) {
                        int idxFinal = idx;
                        fragment = toFragment(() -> fragmentFactory.accept(idxFinal, item), this);
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
                idx++;
            }
        });
    }

    default TSelf RonClick(Runnable handler) {
        ComponentUtil util = internal_target().util();
        HtmlFragment fragment = new HtmlFragment(getParentFragment()) {
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

    default GroupHtmlFragment getParentFragment() {
        return internal_target().getParentFragment();
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
        return VALUE(createValueHandle(value, isLabelProperty));
    }

    default <T> ValueHandle<T> createValueHandle(Supplier<T> value) {
        return createValueHandle(value, true);
    }

    default <T> ValueHandle<T> createValueHandle(Supplier<T> value, boolean isLabelProperty) {
        return internal_target().createValueHandle(value, isLabelProperty);
    }

    default TSelf VALUE(ValueHandle<String> value) {
        ComponentUtil util = internal_target().util();
        HtmlFragment fragment = new HtmlFragment(getParentFragment()) {
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
    default TSelf addLabel(LString label) {
        getParentFragment().addLabel(label);
        return self();
    }

    /**
     * Write text after HTML escaping it and close the current tag
     * 
     * @param unescapedString
     *            String , HTML or plain text or null
     * @return HTMLCanvas , the receiver
     */
    default TSelf content(Supplier<String> unescapedString) {
        return write(unescapedString).close();
    }

    /**
     * Write text after HTML escaping it. No need to close().
     * 
     * @param unescapedString
     *            String , HTML or plain text or null
     * @return HTMLCanvas , the receiver
     */
    default TSelf write(Supplier<String> unescapedString) {
        internal_target().write(unescapedString);
        return self();
    }

    /**
     * Write some text without escaping it
     * 
     * @param text
     *            String , HTML or plain text
     * @return HTMLCanvas , the receiver
     */
    default TSelf writeUnescaped(Supplier<String> text) {
        internal_target().writeUnescaped(text);
        return self();
    }

    default TSelf addAttribute(String key, Supplier<String> value) {
        internal_target().addAttribute(key, value);
        return self();
    }

    default TSelf addAttributeOpt(String key, Supplier<Optional<String>> value) {
        internal_target().addAttributeOpt(key, value);
        return self();
    }

}
