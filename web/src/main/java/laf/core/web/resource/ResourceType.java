package laf.core.web.resource;

import java.util.ArrayList;
import java.util.Collection;

public enum ResourceType {
	JS(null, "application/javascript", ".js"), CSS(null, "text/css", ".css"), SASS(
			CSS, null, ".sass");

	final private String contentType;
	final private String extension;
	final private ResourceType finalType;

	private ResourceType(ResourceType finalType, String contentType,
			String extension) {
		this.finalType = finalType == null ? this : finalType;
		this.contentType = contentType;
		this.extension = extension;
	}

	public String getContentType() {
		return contentType;
	}

	public String getExtension() {
		return extension;
	}

	public static ResourceType fromExtension(String resourceName) {
		for (ResourceType type : values()) {
			if (resourceName.endsWith(type.getExtension())) {
				return type;
			}
		}
		return null;
	}

	public Collection<ResourceType> getSourceTypes() {
		ArrayList<ResourceType> result = new ArrayList<>();
		for (ResourceType type : values()) {
			if (type.getFinalType() == this) {
				result.add(type);
			}
		}
		return result;
	}

	public ResourceType getFinalType() {
		return finalType;
	}
}
