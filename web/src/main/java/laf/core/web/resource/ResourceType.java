package laf.core.web.resource;

public enum ResourceType {
	JS("", ".js"), CSS("", ".css");

	final private String contentType;
	final private String extension;

	private ResourceType(String contentType, String extension) {
		this.contentType = contentType;
		this.extension = extension;
	}

	public String getContentType() {
		return contentType;
	}

	public String getExtension() {
		return extension;
	}
}
