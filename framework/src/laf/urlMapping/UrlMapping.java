package laf.urlMapping;

import java.util.LinkedList;

import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;

@Singleton
@Alternative
public class UrlMapping {

	private LinkedList<UrlMappingRule> systemWideRules = new LinkedList<>();

	public LinkedList<UrlMappingRule> getSystemWideRules() {
		return systemWideRules;
	}

	public void setSystemWideRules(LinkedList<UrlMappingRule> systemWideRules) {
		if (systemWideRules == null) {
			throw new IllegalArgumentException(
					"System wide rules cannot be null");
		}
		this.systemWideRules = systemWideRules;
	}
}
