package com.github.ruediste.rise.component.components;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.component.tree.ValueHandle;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class COptionalInputBase<T> extends Component<COptionalInputBase<T>> {

    private ValueHandle<Optional<T>> value;
    protected T defaultValue;

    protected static abstract class Template<T> extends BootstrapComponentTemplateBase<COptionalInputBase<T>> {

        @Override
        public void doRender(COptionalInputBase<T> component, BootstrapRiseCanvas<?> html) {
            boolean checked = component.getValue().get().isPresent();

            // hidden input to transport checked value
            html.input_checkbox().NAME(util.getParameterKey(component, "checked")).STYLE("display: none").fIf(checked,
                    () -> html.CHECKED());

            // checked version
            html.bInputGroup().CLASS("rise-cOptionalInputBase _checked").fIf(!checked,
                    () -> html.STYLE("display: none"));

            html.bInputGroupAddon().CLASS("_check").input_checkbox().VALUE("true").CHECKED()._bInputGroupAddon();

            renderValueInput(component, html, component.getValue().get().orElse(component.getDefaultValue()));

            html._bInputGroup();

            // unchecked version
            html.bInputGroup().CLASS("rise-cOptionalInputBase _unchecked").fIf(checked,
                    () -> html.STYLE("display: none"));

            html.bInputGroupAddon().CLASS("_check").input_checkbox().VALUE("true")._bInputGroupAddon();

            html.span().CLASS("_placeholder").input_text().DISABLED().BformControl()._span();

            html._bInputGroup();

        }

        protected abstract void renderValueInput(COptionalInputBase<T> component, BootstrapRiseCanvas<?> html, T value);

        @Override
        public void applyValues(COptionalInputBase<T> component) {
            component.getValue()
                    .set(util.getParameterValue(component, "checked").flatMap(x -> extractValue(component)));
        }

        abstract Optional<T> extractValue(COptionalInputBase<T> component);
    }

    public ValueHandle<Optional<T>> getValue() {
        return value;
    }

    public COptionalInputBase<T> value( Supplier<Optional<T>> value) {
        return value(createValueHandle(value, true));
    }

    public COptionalInputBase<T> value(ValueHandle<Optional<T>> value) {
        this.value = value;
        return this;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public COptionalInputBase<T> defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

}
