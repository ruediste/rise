package com.github.ruediste.rise.component.components;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;

import com.github.ruediste.rise.core.CoreRequestInfo;
import com.github.ruediste.rise.core.web.HttpRenderResult;
import com.github.ruediste.rise.core.web.JsonRenderResultFactory;
import com.github.ruediste.rise.integration.BootstrapRiseCanvas;

public class CAutoCompleteTemplate
        extends BootstrapComponentTemplateBase<CAutoComplete<?>> {

    @Override
    public void doRender(CAutoComplete<?> component,
            BootstrapRiseCanvas<?> html) {
        html.input().TYPE("text").BformControl().CLASS("rise_autocomplete")
                .VALUE(component.getText()).TEST_NAME(component.TEST_NAME())
                .NAME(util.getKey(component, "value"))
                .ID(util.getComponentId(component));
    }

    @Override
    public void applyValues(CAutoComplete<?> component) {
        getParameterValue(component, "value").ifPresent(component::setText);
    }

    @Inject
    CoreRequestInfo info;

    @Inject
    JsonRenderResultFactory resultFactory;

    private static class Entry {
        private String label;
        private String value;

        Entry(String label, String value) {
            super();
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
        return resultFactory.jsonRenderResult(items.stream()
                .map(i -> new Entry(component.getSuggestionFunction().apply(i),
                        component.getValueFunction().apply(i)))
                .collect(toList()));
    };

    @Override
    public HttpRenderResult handleAjaxRequest(CAutoComplete<?> component,
            String suffix) throws Throwable {
        return search(component);
    }
}
