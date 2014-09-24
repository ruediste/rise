package laf.core.web.resource.v2;

public interface Resource {
	String getName();

	byte[] getData();

	DataEqualityTracker getDataEqualityTracker();
}
