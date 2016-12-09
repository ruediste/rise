package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.github.ruediste.rendersnakeXT.canvas.Renderable;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.nonReloadable.lambda.Capture;
import com.github.ruediste.rise.util.NOptional;

public class CSelect<T> extends Component<CSelect<T>> {

    public static class Template<T> extends BootstrapComponentTemplateBase<CSelect<T>> {

        @Override
        public void doRender(CSelect<T> component, BootstrapRiseCanvas<?> html) {
            html.select().BformControl().NAME(util.getParameterKey(component, "selected"));
            if (component.isEmptyOptionAllowed())
                html.option().VALUE("-1").render(component.getEmptyOptionRenderer())._option();
            for (int i = 0; i < component.getOptions().size(); i++) {
                CSelectOption<?> option = component.getOptions().get(i);
                html.option().VALUE(Objects.toString(i))
                        .fIf(Objects.equals(component.getSelected().get(), NOptional.of(option.key)),
                                () -> html.SELECTED())
                        .render(option.body)._option();
            }
            html._select();
        }

        @Override
        public void applyValues(CSelect<T> component) {
            NOptional<T> value = util.getParameterValue(component, "selected").map(Integer::parseInt)
                    .map(idx -> idx == -1 ? NOptional.<T> empty() : NOptional.of(component.getOptions().get(idx).key))
                    .orElse(NOptional.empty());
            if (!component.isEmptyOptionAllowed() && !value.isPresent())
                throw new RuntimeException("Empty value is not allowed");
            component.getSelected().set(value);
        }

    }

    public static class CSelectOption<T> {
        public T key;
        public Renderable<BootstrapRiseCanvas<?>> body;

        public CSelectOption() {
        }

        public CSelectOption(T key) {
            this(key, html -> html.write(Objects.toString(key)));
        }

        public CSelectOption(T key, Runnable body) {
            this(key, html -> body.run());
        }

        public CSelectOption(T key, Renderable<BootstrapRiseCanvas<?>> body) {
            this.key = key;
            this.body = body;
        }

    }

    private ValueHandle<NOptional<T>> selected;

    private List<CSelectOption<T>> options;

    private boolean emptyOptionAllowed = true;
    private Renderable<BootstrapRiseCanvas<?>> emptyOptionRenderer = html -> {
    };

    public ValueHandle<NOptional<T>> getSelected() {
        return selected;
    }

    public CSelect() {

    }

    public CSelect(ValueHandle<NOptional<T>> selected) {
        this.selected = selected;
    }

    public CSelect<T> selected(ValueHandle<NOptional<T>> selected) {
        this.selected = selected;
        return this;
    }

    public CSelect<T> selectedMandatory(@Capture Supplier<T> selected) {
        ValueHandle<T> handle = createValueHandle(selected, true);
        this.selected = new ValueHandle<NOptional<T>>() {

            @Override
            public NOptional<T> get() {
                return NOptional.of(handle.get());
            }

            @Override
            public void set(NOptional<T> value) {
                handle.set(value.orElseThrow(() -> new RuntimeException(
                        "Empty value supplied. Did you accidentially allow empty options?")));
            }
        };
        this.emptyOptionAllowed = false;
        return this;
    }

    public CSelect(@Capture Supplier<NOptional<T>> selected) {
        selected(selected);
    }

    public CSelect<T> selected(@Capture Supplier<NOptional<T>> selected) {
        this.selected = createValueHandle(selected, true);
        return this;
    }

    public List<CSelectOption<T>> getOptions() {
        return options;
    }

    public CSelect<T> options(Stream<CSelectOption<T>> options) {
        return options(options.collect(toList()));
    }

    public CSelect<T> options(@SuppressWarnings("unchecked") CSelectOption<T>... options) {
        return options(Arrays.asList(options));
    }

    public CSelect<T> options(List<CSelectOption<T>> options) {
        this.options = options;
        return this;
    }

    public Renderable<BootstrapRiseCanvas<?>> getEmptyOptionRenderer() {
        return emptyOptionRenderer;
    }

    public CSelect<T> emptyOptionRenderer(Renderable<BootstrapRiseCanvas<?>> emptyOptionRenderer) {
        this.emptyOptionRenderer = emptyOptionRenderer;
        return this;
    }

    public boolean isEmptyOptionAllowed() {
        return emptyOptionAllowed;
    }

    public CSelect<T> emptyOptionAllowed(boolean emptyOptionAllowed) {
        this.emptyOptionAllowed = emptyOptionAllowed;
        return this;
    }
}
