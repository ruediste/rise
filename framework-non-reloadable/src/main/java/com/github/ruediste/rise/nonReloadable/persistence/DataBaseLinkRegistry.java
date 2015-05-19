package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.inject.Singleton;

import com.github.ruediste.rise.nonReloadable.NonRestartable;

/**
 * Registry holding the available {@link DataBaseLink}s. Used to register the
 * links in the dynamic injector
 */
@Singleton
@NonRestartable
public class DataBaseLinkRegistry {

	final private HashMap<Class<? extends Annotation>, DataBaseLink> links = new HashMap<>();

	public Iterable<DataBaseLink> getLinks() {
		return links.values();
	}

	public void addLink(DataBaseLink dataBaseLink) {
		links.put(dataBaseLink.getQualifier(), dataBaseLink);
	}

	public DataBaseLink getLink(Class<? extends Annotation> qualifier) {
		return links.get(qualifier);
	}

	public void close() {
		links.values().forEach(DataBaseLink::close);
	}

	public void runSchemaMigrations() {
		links.values().forEach(DataBaseLink::runSchemaMigration);
	}

	public void dropAndCreateSchemas() {
		links.values().forEach(
				link -> link.getPersistenceUnitManager().dropAndCreateSchema());
	}

	public void closePersistenceUnitManagers() {
		links.values()
				.forEach(link -> link.getPersistenceUnitManager().close());
	}
}
