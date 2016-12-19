package com.github.ruediste.rise.es;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import com.github.ruediste.rise.es.api.EsRoot;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.ClasspathResourceIndex;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

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

    public void dropIdexes() {
        Set<String> indices = new HashSet<>();
        forEachEsRoot((cls, root) -> indices.add(root.index()));

        for (String index : indices) {
            log.info("dropping {}", index);
            es.execute(new DeleteIndex.Builder(index + "*").build());
        }
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
                .forEach(s -> initializeIndexTemplate(s, s.substring(prefix.length(), s.length() - suffix.length())));
    }

    private void initializeIndexTemplate(String resource, String name) {
        String mapping = loadResouce(resource);
        JSONArray template;
        try {
            JSONObject parsed = new JSONObject(mapping);
            template = parsed.optJSONArray("template");
            if (template == null) {
                template = new JSONArray().put(parsed.getString("template"));
            }

            for (int i = 0; i < template.length(); i++) {
                String current = template.getString(i);
                parsed.put("template", current);
                es.executeSucessfully(new PutTemplate.Builder(name + i, parsed.toString()).build());
            }
        } catch (JSONException e) {
            throw new RuntimeException("error while parsing template " + resource, e);
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
