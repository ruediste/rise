package com.github.ruediste.rise.es;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.es.api.EsEntity;
import com.github.ruediste.rise.es.api.EsNameHelper;
import com.github.ruediste.rise.util.Try;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Index.Builder;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.Refresh;

@Singleton
public class EsHelper {

    @Inject
    JestClient client;

    @Inject
    Gson gson;

    public <T extends JestResult> T execute(Action<T> clientRequest) {
        try {
            return client.execute(clientRequest);
        } catch (IOException e) {
            throw new RuntimeException("Error while accessing ElasticSearch", e);
        }
    };

    public <T extends JestResult> T executeSucessfully(Action<T> clientRequest) {
        T result = execute(clientRequest);
        assertSuccessful(result);
        return result;
    }

    private <T extends JestResult> void assertSuccessful(T result) {
        if (!result.isSucceeded())
            throw new RuntimeException("ElasticSearch request not sucessful: " + result.getErrorMessage());
    }

    public void store(EsEntity<?> entity) {
        store(entity, false);
    }

    /**
     * Store an entity
     * 
     * @param entity
     *            entity to store
     * @param create
     *            if true, the entity will only be created freshly, never
     *            updated
     * @return true if the operation was successful. Only false if create is
     *         true and the document already exists
     * @throws RuntimeException
     *             if the store operation fails
     */
    public boolean store(EsEntity<?> entity, boolean create) {
        Builder builder;
        {
            builder = new Index.Builder(entity).index(EsNameHelper.index(entity.getClass()))
                    .type(EsNameHelper.type(entity.getClass())).id(entity.getId());
        }
        if (create)
            builder.setParameter("op_type", "create");
        if (entity.getVersion() != null)
            builder.setParameter("version", entity.getVersion().toString());

        DocumentResult result = execute(builder.build());

        if (result.isSucceeded()) {
            if (Strings.isNullOrEmpty(entity.getId()))
                entity.setId(result.getId());
            entity.setVersion(result.getJsonObject().get("_version").getAsLong());
        }
        if (create)
            return result.isSucceeded();
        assertSuccessful(result);
        return true;
    }

    public <T extends EsEntity<?>> Optional<T> get(Class<T> cls, String id) {
        DocumentResult result = execute(
                new Get.Builder(EsNameHelper.index(cls), id).type(EsNameHelper.type(cls)).build());
        if (result.isSucceeded()) {
            T entity = result.getSourceAsObject(cls);
            entity.setId(id);
            entity.setVersion(result.getJsonObject().get("_version").getAsLong());
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public <T extends EsEntity<?>> List<T> loadAll(Class<T> entityClass) {
        return search("{\"version\":true, \"query\":{\"match_all\": {} }}", entityClass);
    }

    public <T extends EsEntity<?>> List<T> search(String query, Class<T> entityClass) {
        SearchResult result = executeSucessfully(new Search.Builder(query).addIndex(EsNameHelper.index(entityClass))
                .addType(EsNameHelper.type(entityClass)).build());

        return hitStream(result, entityClass).map(h -> h.getObject()).collect(toList());
    }

    public static class Hit<T> {
        public JsonObject hitObj;
        private Gson gson;
        private Class<T> entityClass;

        public Hit(JsonObject hitObj, Gson gson, Class<T> entityClass) {
            this.hitObj = hitObj;
            this.gson = gson;
            this.entityClass = entityClass;
        }

        public T getObject() {
            T result = gson.fromJson(hitObj.get("_source"), entityClass);
            if (EsEntity.class.isAssignableFrom(entityClass)) {
                if (result == null) {
                    try {
                        result = entityClass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException("Error while instantiating " + entityClass.getName());
                    }

                }
                ((EsEntity<?>) result).setId(getId());
                T resultFinal = result;
                tryGetVersion().ifPresent(x -> ((EsEntity<?>) resultFinal).setVersion(x));

            }
            return result;
        }

        public String getId() {
            return hitObj.get("_id").getAsString();
        }

        public long getVersion() {
            return tryGetVersion().get();
        }

        public Try<Long> tryGetVersion() {
            JsonElement tmp = hitObj.get("_version");
            if (tmp == null) {
                return Try.failure(() -> new RuntimeException(
                        "No version field found in ES response. Did you specify the field in the query?"));
            } else
                return Try.of(tmp.getAsLong());
        }
    }

    public <T> List<T> hitList(SearchResult result, Class<T> objClass) {
        return hitStream(result, objClass).map(x -> x.getObject()).collect(toList());
    }

    public <T> Stream<Hit<T>> hitStream(SearchResult result, Class<T> objClass) {
        JsonObject jsonObject = result.getJsonObject();
        JsonArray hitList = jsonObject.getAsJsonObject("hits").get("hits").getAsJsonArray();
        return StreamSupport.stream(hitList.spliterator(), false)
                .map(hit -> new Hit<>(hit.getAsJsonObject(), gson, objClass));
    }

    public void refresh(Class<?> entityClass) {
        executeSucessfully(new Refresh.Builder().addIndex(EsNameHelper.indexPattern(entityClass)).build());
    }

    public void delete(EsEntity<?> entity) {
        delete(entity.getClass(), entity.getId());
    }

    @SuppressWarnings("rawtypes")
    public void delete(Class<? extends EsEntity> entityClass, String id) {
        String index = EsNameHelper.index(entityClass);
        String type = EsNameHelper.type(entityClass);
        try {
            executeSucessfully(new Delete.Builder(id).index(index).type(type).build());
        } catch (Throwable t) {
            throw new RuntimeException(
                    "Error while deleting entity. id: " + id + " index: " + index + " type: " + type);
        }
    }

}
