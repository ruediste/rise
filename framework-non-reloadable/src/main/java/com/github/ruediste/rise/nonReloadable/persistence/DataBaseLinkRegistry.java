package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Registry holding the available {@link DataBaseLink}s. Used to register the
 * links in the dynamic injector
 */
@Singleton
@NonRestartable
public class DataBaseLinkRegistry {

    @Inject
    CoreConfigurationNonRestartable configurationNonRestartable;

    final private BiMap<Class<? extends Annotation>, Integer> linkNrs = HashBiMap
            .create();

    private final ArrayList<DataBaseLink> links = new ArrayList<>();
    final private HashMap<Class<? extends Annotation>, DataBaseLink> linkMap = new HashMap<>();

    public Class<? extends Annotation> getQualifierByNr(int linkNr) {
        return linkNrs.inverse().get(linkNr);
    }

    public int getQualifierNr(Class<? extends Annotation> qualifier) {
        return linkNrs.get(qualifier);
    }

    public Collection<DataBaseLink> getLinks() {
        return linkMap.values();
    }

    public void addLink(DataBaseLink dataBaseLink) {
        linkMap.put(dataBaseLink.getQualifier(), dataBaseLink);
        links.add(dataBaseLink);
    }

    public DataBaseLink getLink(Class<? extends Annotation> qualifier) {
        return linkMap.get(qualifier);
    }

    public void close() {
        linkMap.values().forEach(DataBaseLink::close);
    }

    /**
     * Run the schema migration if enabled by configuration. Otherwise does not
     * perform any action
     */
    public void runSchemaMigrations() {
        if (configurationNonRestartable.isRunSchemaMigration()) {
            linkMap.values().forEach(DataBaseLink::runSchemaMigration);
        }
    }

    public void dropAndCreateSchemas() {
        linkMap.values().forEach(
                link -> link.getPersistenceUnitManager().dropAndCreateSchema());
    }

    public void closePersistenceUnitManagers() {
        linkMap.values()
                .forEach(link -> link.getPersistenceUnitManager().close());
    }
}
