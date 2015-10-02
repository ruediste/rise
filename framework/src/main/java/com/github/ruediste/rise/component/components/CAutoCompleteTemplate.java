package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.crypto.Mac;
import javax.inject.Inject;

import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.JsonRenderResultFactory;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.nonReloadable.SignatureHelper;
import com.google.common.base.Charsets;

public class CAutoCompleteTemplate
        extends BootstrapComponentTemplateBase<CAutoComplete<?>> {

    @Inject
    SignatureHelper signatureHelper;

    @Inject
    CoreRequestInfo info;

    @Override
    public void doRender(CAutoComplete<?> component,
            BootstrapRiseCanvas<?> html) {
        doRenderImpl(component, html);
    }

    private Consumer<Mac> hashContext(Component component) {
        return mac -> {
            mac.update(info.getSessionId().getBytes(Charsets.UTF_8));
            mac.update(getComponentId(component).getBytes(Charsets.UTF_8));
        };
    }

    public <T> void doRenderImpl(CAutoComplete<T> component,
            BootstrapRiseCanvas<?> html) {
        String value;
        Optional<String> itemStr = Optional.empty();
        if (component.isItemChosen()) {
            T item = component.getChosenItem();
            value = component.getValueFunction().apply(item);
            Consumer<Mac> contextHasher = mac -> {
                mac.update(info.getSessionId().getBytes(Charsets.UTF_8));
                mac.update(getComponentId(component).getBytes(Charsets.UTF_8));
            };
            signatureHelper.serializeSigned(item, hashContext(component));

        } else {
            value = component.getText();
        }
        html.input().TYPE("text").BformControl().CLASS("rise_autocomplete")
                .VALUE(value).TEST_NAME(component.TEST_NAME())
                .NAME(util.getKey(component, "text"))
                .ID(util.getComponentId(component));

        if (component.isItemChosen()) {
            html.input().TYPE("text").STYLE("display: none;").VALUE(":")
                    .NAME(util.getKey(component, "chosenItem"));
        }

    }

    @Override
    public void applyValues(CAutoComplete<?> component) {
        getParameterValue(component, "text").ifPresent(component::setText);
    }

    @Inject
    JsonRenderResultFactory resultFactory;

    private static class Entry {
        private String label;
        private String value;

        Entry(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private <T> HttpRenderResult search(CAutoComplete<T> component) {
        List<T> items = component.getSearchFunction()
                .apply(info.getRequest().getParameter("term"));
        return resultFactory.jsonRenderResult(items.stream().map(i -> {
            Map<String, Object> result = new HashMap<>();
            result.put("label", component.getSuggestionFunction().apply(i));
            result.put("value", component.getValueFunction().apply(i));
            return result;
        }).collect(toList()));
    };

    @Override
    public HttpRenderResult handleAjaxRequest(CAutoComplete<?> component,
            String suffix) throws Throwable {
        return search(component);
    }
}
