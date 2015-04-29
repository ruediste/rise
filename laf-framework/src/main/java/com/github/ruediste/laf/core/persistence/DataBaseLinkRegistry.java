package com.github.ruediste.laf.core.persistence;

import java.util.ArrayList;

import javax.inject.Singleton;

/**
 * Registry holding the available {@link DataBaseLink}s. Used to register the
 * links in the dynamic injector
 */
@Singleton
public class DataBaseLinkRegistry {

	final private ArrayList<DataBaseLink> links = new ArrayList<DataBaseLink>();

	public ArrayList<DataBaseLink> getLinks() {
		return links;
	}

	public void initializeDataSources() {
		links.forEach(DataBaseLink::initializeDataSource);
	}
}
