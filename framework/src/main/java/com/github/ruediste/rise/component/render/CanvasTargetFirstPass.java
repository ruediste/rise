package com.github.ruediste.rise.component.render;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rendersnakeXT.canvas.CanvasTargetToConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducer;
import com.github.ruediste.rendersnakeXT.canvas.HtmlProducerHtmlCanvasTarget;
import com.github.ruediste.rise.api.ViewComponentBase;
import com.github.ruediste.rise.component.ComponentTemplateIndex;
import com.github.ruediste.rise.component.components.IComponentTemplate;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.HidingComponent;
import com.github.ruediste.rise.component.tree.RootComponent;
import com.github.ruediste.rise.integration.RiseCanvas;
import com.github.ruediste.rise.util.NOptional;
import com.github.ruediste.rise.util.Pair;

public class CanvasTargetFirstPass extends HtmlProducerHtmlCanvasTarget implements RiseCanvasTarget {

    @Inject
    ComponentTemplateIndex componentTemplateIndex;

    private final Component<?> root = new RootComponent();

    private Component<?> parent = getRoot();
    private Component<?> previousParent;
    private Map<Class<?>, Integer> keyMap = new HashMap<>();
    private Map<Object, Component<?>> previousChildren = new HashMap<>();

    private ViewComponentBase<?> view;

    @Override
    public void addAttributePlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        this.checkAttributesUncommited();
        Component<?> currentParent = parent;
        this.addProducer(new HtmlProducer() {

            @Override
            public void produce(HtmlConsumer consumer) {
                CanvasTargetForAttributePlaceholder target = new CanvasTargetForAttributePlaceholder() {

                    @Override
                    protected void write(String str) {
                        consumer.accept(str);
                    }

                    @Override
                    public Component<?> getParent() {
                        return currentParent;
                    }
                };
                html.setTarget(target);
                placeholder.run();
            }
        }, false);
    }

    @Override
    public void addPlaceholder(RiseCanvas<?> html, Runnable placeholder) {
        Component<?> currentParent = parent;
        this.addProducer(new HtmlProducer() {

            @Override
            public void produce(HtmlConsumer consumer) {
                CanvasTargetToConsumer target = new CanvasTargetToConsumer(consumer);
                target.captureStartStackTraces = captureStartStackTraces;
                html.setTarget(new CanvasTargetForPlaceholder(target, currentParent));
                placeholder.run();
                target.commitAttributes();
                target.checkAllTagsClosed();
                target.flush();
            }
        }, true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void add(RiseCanvas<?> html, Component<?> component) {

        // merge state
        Component<?> previous = null;
        {
            NOptional<Object> key = component.key();
            if (!key.isPresent()) {
                Integer idx = keyMap.compute(component.getClass(), (k, v) -> v == null ? 0 : v + 1);
                key = NOptional.of(Pair.of(component.getClass(), idx));
                component.setKey(key);
            }
            if (previousParent != null) {
                previous = previousChildren.get(key);
                if (previous != null && previous.getClass().equals(component.getClass())) {
                    // keep children of hidden hiding components, such that
                    // their state is not lost
                    if (component instanceof HidingComponent) {
                        HidingComponent hidingComponent = (HidingComponent) component;
                        if (hidingComponent.isHidden()) {
                            component.getChildren().addAll(previous.getChildren());
                        }
                    }

                    Class<?> cls = previous.getClass();
                    while (cls != null) {
                        try {
                            for (Field field : cls.getDeclaredFields()) {
                                if (Modifier.isStatic(field.getModifiers()))
                                    continue;
                                ComponentState componentState = field.getAnnotation(ComponentState.class);
                                if (componentState == null)
                                    continue;
                                field.setAccessible(true);
                                if (!componentState.alwaysOverwrite() && Optional.class.equals(field.getType())) {
                                    Optional<?> currentValue;
                                    currentValue = (Optional<?>) field.get(component);
                                    // only overwrite Optionals if the are NOT
                                    // present
                                    if (currentValue.isPresent())
                                        continue;
                                }

                                // copy value
                                Object value = field.get(previous);
                                field.set(component, value);
                            }
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            throw new RuntimeException("Unable to copy previous component value over to new component",
                                    e);
                        }

                        cls = cls.getSuperclass();
                    }
                }
            }
        }

        // add to tree
        component.setView(getView());
        component.setParent(parent);
        parent.getChildren().add(component);

        // render component
        Component<?> oldParent = parent;
        Component<?> oldPreviousParent = previousParent;
        Map<Object, Component<?>> oldPreviousChildren = previousChildren;
        Map<Class<?>, Integer> oldKeyMap = keyMap;
        try {
            parent = component;
            previousParent = previous;
            previousChildren = calculateChildrenMap(previous);
            keyMap = new HashMap<>();

            // render component
            ((IComponentTemplate) componentTemplateIndex.getTemplate(component).get()).doRender(component, html);
        } finally {
            parent = oldParent;
            previousParent = oldPreviousParent;
            previousChildren = oldPreviousChildren;
            keyMap = oldKeyMap;
        }
    }

    public Component<?> getRoot() {
        return root;
    }

    public void setPreviousRoot(Component<?> previousRoot) {
        this.previousParent = previousRoot;
        this.previousChildren = calculateChildrenMap(previousRoot);
    }

    private Map<Object, Component<?>> calculateChildrenMap(Component<?> component) {
        if (component == null)
            return Collections.emptyMap();
        return component.getChildren().stream().collect(toMap(x -> x.key(), x -> x));
    }

    @Override
    public ViewComponentBase<?> getView() {
        return view;
    }

    @Override
    public void setView(ViewComponentBase<?> view) {
        this.view = view;
    }

    @Override
    public Component<?> getParent() {
        return parent;
    }

}
