package com.github.ruediste.rise.crud;

import java.util.ArrayList;
import java.util.function.Function;

import javax.inject.Singleton;

import com.github.ruediste.c3java.properties.PropertyDeclaration;

@Singleton
public class CrudPropertyFilters {

    private final ArrayList<Function<PropertyDeclaration, CrudPropertyFilter>> filterFactories = new ArrayList<>();

    public ArrayList<Function<PropertyDeclaration, CrudPropertyFilter>> getFilterFactories() {
        return filterFactories;
    }

    public CrudPropertyFilter createPropertyFilter(PropertyDeclaration decl) {
        return filterFactories
                .stream()
                .map(x -> x.apply(decl))
                .filter(x -> x != null)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException(
                                "No FilterFactory found for " + decl));
    }
}
