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

public interface FragmentCanvas<TSelf extends FragmentCanvas<TSelf>> extends Html5Canvas<TSelf> {

    @Override
    FragmentCanvasTarget internal_target();

    default TSelf write(HtmlProducer producer) {
        internal_target().commitAttributes();
        internal_target().addProducer(producer);
        return self();
    }

    default TSelf write(HtmlFragment fragment) {
        fragment.setParent(internal_target().getParentFragment());
        return write(fragment.getHtmlProducer());
    }

    default HtmlFragment toFragment(HtmlFragment parent, Runnable renderer) {
        return internal_target().toFragment(parent, renderer);
    }

    /**
     * Render ifTrue of ifFalse, depending on the condition.
     * 
     * Both cases are always present in the html structure.
     */
    default TSelf fIf(Supplier<Boolean> condition, Runnable ifTrue, Runnable ifFalse) {
        return write(new HtmlFragment() {
            boolean currentState;
            HtmlFragment trueFragment = toFragment(this, ifTrue);
            HtmlFragment falseFragment = toFragment(this, ifFalse);

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
        return write(new HtmlFragment() {
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
                    currentFragment = currentState ? toFragment(this, ifTrue) : toFragment(this, ifFalse);
                    arg.structureUpdated();
                }
            }

        });
    }

    default <T> TSelf fForEach(Iterable<T> items, Consumer<T> fragmentFactory) {
        return fForEach(() -> items, fragmentFactory);
    }

    default <T> TSelf fForEach(Supplier<Iterable<T>> items, Consumer<T> fragmentFactory) {
        return write(new HtmlFragment() {
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
                        fragment = toFragment(this, () -> fragmentFactory.accept(item));
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
        new HtmlFragment(internal_target().getParentFragment()) {
            @Override
            public void processActions() {
                // TODO check if handler should be executed
                handler.run();
            }
        };
        return DATA("rise-on-click", "0");
    }

    default TSelf VALUE(Supplier<String> value) {
        return self();
    }

    default TSelf VALUE(ValueHandle<String> value) {
        new HtmlFragment(internal_target().getParentFragment()) {
            @Override
            public void applyValues() {
                // TODO extract from parameters
                value.set("abc");
            }
        };
        return addAttribute("VALUE", value).DATA("rise-send-value", /* TODO: */ "0");
    }

}
