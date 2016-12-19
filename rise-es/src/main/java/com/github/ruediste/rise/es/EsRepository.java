package com.github.ruediste.rise.es;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.es.api.EsEntity;
import com.google.common.reflect.TypeToken;

public class EsRepository<T extends EsEntity> {

    @Inject
    protected EsHelper es;

    protected Class<T> entityClass;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public EsRepository() {
        entityClass = (Class) TypeToken.of(getClass()).resolveType(EsRepository.class.getTypeParameters()[0])
                .getRawType();
    }

    public Optional<T> get(String id) {
        return es.get(entityClass, id);
    }

    public List<String> getAllIds() {
        return es.search("{\"query\": {\"match_all\":{}}, \"fields\":[\"_id\"]}", entityClass).stream()
                .map(x -> x.getId()).collect(toList());
    }

    public void store(T entity) {
        wrapUpdate(entity, () -> es.store(entity));
    }

    public void delete(T entity) {
        wrapUpdate(entity, () -> es.delete(entity));
    }

    protected void wrapUpdate(T entity, Runnable operation) {
        operation.run();
    }

    public List<T> loadAll() {
        return es.loadAll(entityClass);
    }

    public void refresh() {
        es.refresh(entityClass);
    }
}
