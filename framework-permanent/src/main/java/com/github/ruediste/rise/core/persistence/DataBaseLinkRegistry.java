package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.inject.Singleton;

import com.github.ruediste.rise.core.Permanent;

/**
 * Registry holding the available {@link DataBaseLink}s. Used to register the
 * links in the dynamic injector
 */
@Singleton
@Permanent
public class DataBaseLinkRegistry {

	final private HashMap<Class<? extends Annotation>, DataBaseLink> links = new HashMap<>();

	public Iterable<DataBaseLink> getLinks() {
		return links.values();
	}

	public void initializeDataSources() {
		links.values().forEach(DataBaseLink::initializeDataSource);
	}

	public void addLink(DataBaseLink dataBaseLink) {
		links.put(dataBaseLink.getQualifier(), dataBaseLink);
	}

	public DataBaseLink getLink(Class<? extends Annotation> qualifier) {
		return links.get(qualifier);
	}

}
