package com.github.ruediste.rise.es;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.github.ruediste.rise.es.api.EsEntity;

import jersey.repackaged.com.google.common.base.Objects;

public interface EsListPostProcessor<T extends EsEntity<T>> extends Function<List<T>, List<T>> {

    static <T extends EsEntity<T>> EsListPostProcessor<T> none() {
        return x -> x;
    }

    static <T extends EsEntity<T>> EsListPostProcessor<T> adding(T entity) {

        return list -> {
            if (!list.stream().anyMatch(x -> Objects.equal(x.getId(), entity.getId()))) {
                ArrayList<T> tmp = new ArrayList<>();
                tmp.add(entity);
                tmp.addAll(list);
                return tmp;
            }
            return list;
        };

    }

    static <T extends EsEntity<T>> EsListPostProcessor<T> removing(T entity) {
        return removingId(entity.getId());
    }

    static <T extends EsEntity<T>> EsListPostProcessor<T> removingId(String id) {
        return list -> list.stream().filter(x -> !Objects.equal(x.getId(), id)).collect(toList());
    }

    static <T extends EsEntity<T>> EsListPostProcessor<T> addingOrReplacing(T entity) {
        return list -> {
            ArrayList<T> tmp = new ArrayList<>();
            boolean found = false;
            for (T x : list) {
                if (Objects.equal(x.getId(), entity.getId())) {
                    found = true;
                    tmp.add(entity);
                } else
                    tmp.add(x);
            }
            if (!found)
                tmp.add(entity);
            return tmp;
        };
    }
}
