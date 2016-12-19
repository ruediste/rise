package com.github.ruediste.rise.es;

import java.util.Optional;

import javax.inject.Inject;

import com.github.ruediste.rise.es.api.EsEntity;
import com.google.common.reflect.TypeToken;

public abstract class EsSingletonRepository<T extends EsEntity> {

    @Inject
    EsHelper es;
    private Class<T> entityClass;
    private String id;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public EsSingletonRepository() {
        entityClass = (Class) TypeToken.of(this.getClass())
                .resolveType(EsSingletonRepository.class.getTypeParameters()[0]).getRawType();
        id = entityClass.getName();
    }

    public T get() {
        Optional<T> result = es.get(entityClass, id);
        if (!result.isPresent()) {
            T instance = initialValue();
            instance.setId(id);
            if (!es.store(instance, true)) {
                // document was created in the mean time, load and return
                return es.get(entityClass, id).get();
            } else
                return instance;
        }
        return result.get();
    }

    public void store(T entity) {
        entity.setId(id);
        wrapUpdate(entity, () -> es.store(entity));
    }

    protected void wrapUpdate(T entity, Runnable operation) {
        operation.run();
    }

    protected abstract T initialValue();
}
