package com.github.ruediste.rise.component.reload;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.github.ruediste.rise.core.scopes.RequestScoped;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

@RequestScoped
public class PageReloadRequest {
    private long pageNr;
    private long fragmentNr;
    private Multimap<String, Object> parameterData;

    public long getPageNr() {
        return pageNr;
    }

    public void setPageNr(long pageNr) {
        this.pageNr = pageNr;
    }

    public long getFragmentNr() {
        return fragmentNr;
    }

    public void setFragmentNr(long componentNr) {
        this.fragmentNr = componentNr;
    }

    /**
     * The data sent along with the reload request
     */
    public Multimap<String, Object> getParameterData() {
        return parameterData;
    }

    public void setParameterData(Multimap<String, Object> data) {
        this.parameterData = data;
    }

    public Optional<Object> getParameterObject(String key) {
        return Optional.ofNullable(Iterables.getFirst(parameterData.get(key), null));
    }

    public Optional<String> getParameterValue(String key) {
        return getParameterObject(key).map(x -> Objects.toString(x));
    }

    public Collection<Object> getParameterObjects(String key) {
        return parameterData.get(key);
    }

    public List<String> getParameterValues(String key) {
        return getParameterObjects(key).stream().map(x -> (String) x).collect(toList());
    }

    public boolean isParameterDefined(String key) {
        return parameterData.containsKey(key);
    }
}
