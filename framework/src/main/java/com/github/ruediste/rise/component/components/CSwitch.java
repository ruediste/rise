package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ComponentBase;
import com.google.common.base.Objects;

@DefaultTemplate(CSwitchTemplate.class)
public class CSwitch<T> extends ComponentBase<CSwitch<T>> {

    private interface Case {
        void onEnter();

        void onLeave();

        Component getComponent();
    }

    private T option;
    private Case currentCase;

    final private HashMap<T, Case> cases = new HashMap<>();

    @Override
    public Iterable<Component> getChildren() {
        return cases.values().stream().map(Case::getComponent)
                .filter(x -> x != null).collect(toList());
    }

    @Override
    public void childRemoved(Component child) {
        throw new UnsupportedOperationException();
    }

    public CSwitch<T> put(T option, Component component) {
        cases.put(option, new Case() {

            @Override
            public void onEnter() {
            }

            @Override
            public void onLeave() {
            }

            @Override
            public Component getComponent() {
                return component;
            }
        });
        return this;
    }

    public CSwitch<T> put(T option, Supplier<Component> componentSupplier) {
        cases.put(option, new Case() {
            Component component;

            @Override
            public void onEnter() {
                component = componentSupplier.get();
            }

            @Override
            public void onLeave() {
                component = null;
            }

            @Override
            public Component getComponent() {
                return component;
            }
        });
        return this;
    }

    public T getOption() {
        return option;
    }

    public void setOption(T option) {
        if (Objects.equal(option, this.option))
            return;
        if (getCurrentCase() != null)
            getCurrentCase().onLeave();
        this.option = option;
        this.currentCase = cases.get(option);
        if (currentCase == null)
            throw new RuntimeException("No case for option " + option
                    + " registered");
        currentCase.onEnter();
    }

    public CSwitch<T> bind(Supplier<T> supplier) {
        bind(x -> x.setOption(supplier.get()));
        return this;
    }

    public Optional<Component> getCurrentComponent() {
        if (currentCase == null)
            return Optional.empty();
        return Optional.ofNullable(currentCase.getComponent());
    }

    public Case getCurrentCase() {
        return currentCase;
    }

}
