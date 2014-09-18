package laf.core.web.resource;

import java.util.Objects;

public class ResourceType {

	final private String identifier;

	public static final ResourceType CSS = valueOf("css");
	public static final ResourceType JS = valueOf("js");
	public static final ResourceType SASS = valueOf("sass");

	private ResourceType(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		ResourceType other = (ResourceType) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {
		return identifier;
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}

	/**
	 * Return a new or existing resource type of the given identifier
	 */
	public static ResourceType valueOf(String identifier) {
		return new ResourceType(identifier);
	}

	/**
	 * Create a new {@link ResourceType} using the extension of the provided
	 * resource name.
	 */
	public static ResourceType fromExtension(String resourceName) {
		int idx = resourceName.lastIndexOf('.');
		if (idx < 0 || idx > resourceName.length() - 2) {
			throw new RuntimeException("No extension found in " + resourceName);
		}
		return valueOf(resourceName.substring(idx + 1));
	}
}
