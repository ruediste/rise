package com.github.ruediste.rise.es;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.inject.Inject;
import javax.inject.Provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.github.ruediste.rise.es.api.EsNameHelper;
import com.github.ruediste.rise.es.api.EsRoot;
import com.github.ruediste.rise.es.api.IndexSuffixExtractor;
import com.github.ruediste.rise.es.migration.MigrationTargetESBase;
import com.github.ruediste.rise.es.migration.MigrationTaskESIndexTemplate;
import com.github.ruediste.rise.migration.MigrationEngine;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.ClasspathResourceIndex;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.template.PutTemplate;

public class EsManagementService {

    @Inject
    EsHelper es;

    @Inject
    ClassHierarchyIndex index;

    @Inject
    ClasspathResourceIndex cri;

    @Inject
    ClassLoader cl;

    @Inject
    Logger log;

    @Inject
    Provider<MigrationTaskESIndexTemplate> taskProvider;

    public void addEsIndexTasks(MigrationEngine engine, String resourceGlob,
            Class<? extends MigrationTargetESBase> target) {
        cri.getResourcesByGlob(resourceGlob).stream().map(name -> taskProvider.get().initialize(name, target))
                .forEach(engine::addTask);
    }

    public void createIndices() {
        for (String index : getEsIndices(false)) {
            log.info("creating {}", index);
            es.execute(new CreateIndex.Builder(index).build());
        }
    }

    public void dropIdexes() {
        for (String index : getEsIndices(true)) {
            log.info("dropping {}", index);
            es.execute(new DeleteIndex.Builder(index).build());
        }
    }

    /**
     * Get all indices.
     */
    private Set<String> getEsIndices(boolean includeIndicesWithSuffix) {
        Set<String> indices = new HashSet<>();

        forEachEsRoot((cls, root) -> {
            String index = root.index();
            if ("".equals(index)) {
                index = EsNameHelper.DEFAULT_INDEX;
            }
            if (IndexSuffixExtractor.class.equals(root.suffixExtractor()))
                indices.add(index);
            else {
                if (includeIndicesWithSuffix) {
                    indices.add(index + "-*");
                }
            }
        });
        return indices;
    }

    private void forEachEsRoot(BiConsumer<Class<?>, EsRoot> consumer) {
        for (Class<?> esClass : getEsClasses()) {
            EsRoot esRoot = esClass.getAnnotation(EsRoot.class);
            if (esRoot == null)
                continue;
            consumer.accept(esClass, esRoot);
        }

    }

    private Set<Class<?>> getEsClasses() {
        Set<Class<?>> esClasses = new HashSet<>();
        index.getClassesByAnnotation(EsRoot.class, cl).forEach(root -> {
            esClasses.add(root);
            index.getAllChildClasses(root, cl).forEach(esClasses::add);
        });
        return esClasses;
    }

    public void initializeIndexTemplates() {
        String prefix = "indexTemplates/";
        String suffix = ".json";
        cri.getResourcesByGlob(prefix + "*" + suffix).stream()
                .forEach(s -> initializeIndexTemplate(s.substring(prefix.length(), s.length() - suffix.length()), s));
    }

    private void initializeIndexTemplate(String name, String resource) {
        String mapping = loadResouce(resource);
        JSONObject parsed;
        try {
            parsed = new JSONObject(mapping);
        } catch (JSONException e) {
            throw new RuntimeException("error while parsing template " + resource, e);
        }
        initializeIndexTemplate(name, parsed);
    }

    public void initializeIndexTemplate(String name, JSONObject parsed) {
        JSONArray template = parsed.optJSONArray("template");
        if (template == null) {
            template = new JSONArray().put(parsed.getString("template"));
        }

        for (int i = 0; i < template.length(); i++) {
            String templateName = name + i;
            log.info("putting index template {}", templateName);
            String current = template.getString(i);
            parsed.put("template", current);
            es.executeSucessfully(new PutTemplate.Builder(templateName, parsed.toString()).build());
        }
    }

    private String loadResouce(String name) {
        try (InputStream in = cl.getResourceAsStream(name)) {
            if (in == null)
                throw new RuntimeException("Index template " + name + " not found");
            return new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
