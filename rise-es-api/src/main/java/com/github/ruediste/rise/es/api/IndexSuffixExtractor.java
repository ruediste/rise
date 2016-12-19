package com.github.ruediste.rise.es.api;

public interface IndexSuffixExtractor<T> {
    String extract(T entity);
}
