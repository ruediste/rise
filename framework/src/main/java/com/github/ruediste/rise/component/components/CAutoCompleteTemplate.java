package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.crypto.Mac;
import javax.inject.Inject;

import com.github.ruediste.rise.component.ComponentRequestInfo;
import com.github.ruediste.rise.component.components.CAutoComplete.AutoCompleteValue;
import com.github.ruediste.rise.component.components.CAutoComplete.CAutoCompleteParameters;
import com.github.ruediste.rise.component.tree.Component;
import com.github.ruediste.rise.core.CoreConfiguration;
import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.JsonRenderResultFactory;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;
import com.github.ruediste.rise.nonReloadable.SignatureHelper;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

public class CAutoCompleteTemplate
        extends BootstrapComponentTemplateBase<CAutoComplete<?, ?>> {

    @Inject
    SignatureHelper signatureHelper;

    @Inject
    CoreRequestInfo info;

    @Inject
    ComponentRequestInfo componentRequestInfo;

    @Override
    public void doRender(CAutoComplete<?, ?> component,
            BootstrapRiseCanvas<?> html) {
        doRenderImpl(component, html);
    }

    private Consumer<Mac> hashContext(Component component) {
        return mac -> {
            mac.update(info.getSessionId().getBytes(Charsets.UTF_8));
            mac.update(String.valueOf(componentRequestInfo.getPageHandle().id)
                    .getBytes(Charsets.UTF_8));
            mac.update(getComponentId(component).getBytes(Charsets.UTF_8));
        };
    }

    public <T> void doRenderImpl(CAutoComplete<T, ?> component,
            BootstrapRiseCanvas<?> html) {
        html.input().TYPE("text").BformControl().CLASS("rise_autocomplete")
                .rCOMPONENT_ATTRIBUTES(component)
                .NAME(util.getKey(component, "text"))
                .DATA("rise-int-source", getAjaxUrl(component));

        if (component.isItemChosen()) {
            T item = component.getValue().getItem();
            html.VALUE(component.getParameters().getValue(item))
                    .DATA("rise-int-chosen-item",
                            BaseEncoding.base64()
                                    .encode(signatureHelper.serializeSigned(
                                            component.getParameters()
                                                    .getId(item),
                                            hashContext(component))));
        } else {
            html.VALUE(component.getText());
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void applyValues(CAutoComplete component) {
        Optional<String> chosenItem = getParameterValue(component,
                "riseIntChosenItem");
        if (chosenItem.isPresent()) {
            component
                    .setValue(
                            AutoCompleteValue.ofItem(component.getParameters()
                                    .load(signatureHelper
                                            .deserializeSigned(
                                                    BaseEncoding.base64()
                                                            .decode(chosenItem
                                                                    .get()),
                                            hashContext(component)))));
        } else {
            component.setText(getParameterValue(component, "text").get());
        }
    }

    @Inject
    JsonRenderResultFactory resultFactory;

    @Inject
    CoreConfiguration config;

    private <T> HttpRenderResult search(CAutoComplete<T, ?> component) {
        CAutoCompleteParameters<T, ?> parameters = component.getParameters();
        List<T> items = parameters
                .search(info.getRequest().getParameter("term"));
        return resultFactory.jsonRenderResult(items.stream().map(i -> {
            Map<String, Object> result = new HashMap<>();
            result.put("label", parameters.getSuggestion(i));
            result.put("value", parameters.getValue(i));
            if (config.isRenderTestName()) {
                result.put("testName", parameters.getTestName(i));
            }
            result.put("id",
                    BaseEncoding.base64()
                            .encode(signatureHelper.serializeSigned(
                                    parameters.getId(i),
                                    hashContext(component))));
            return result;
        }).collect(toList()));
    };

    @Override
    public HttpRenderResult handleAjaxRequest(CAutoComplete<?, ?> component,
            String suffix) throws Throwable {
        return search(component);
    }
}
