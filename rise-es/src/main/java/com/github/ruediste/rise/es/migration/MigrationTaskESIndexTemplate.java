package com.github.ruediste.rise.es.migration;

import java.util.Set;

import javax.inject.Inject;

import org.json.JSONObject;

import com.github.ruediste.rise.es.EsManagementService;
import com.github.ruediste.rise.migration.MigrationTarget;
import com.github.ruediste.rise.migration.MigrationTaskNoAutoDiscover;

/**
 * Migration task initialized from a json file, replacing a set of index
 * templates. The root object of the file must be an object with the template
 * names as keys.
 */
@MigrationTaskNoAutoDiscover
public class MigrationTaskESIndexTemplate extends MigrationTaskESBase {

    @Inject
    EsManagementService service;

    private JSONObject json;

    /**
     * Initialize this task
     * 
     * @param sqlResourceName
     *            name in the format <path>/<timestamp>.<author>. <description>.
     *            sql
     */
    public MigrationTaskESIndexTemplate initialize(String sqlResourceName, Class<? extends MigrationTarget<?>> target) {
        initializeFromResourceName(target, sqlResourceName);
        json = new JSONObject(loadResource(sqlResourceName));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws Throwable {
        for (String key : (Set<String>) json.keySet()) {
            JSONObject template = json.getJSONObject(key);
            service.initializeIndexTemplate(key, template);
        }
    }
}
