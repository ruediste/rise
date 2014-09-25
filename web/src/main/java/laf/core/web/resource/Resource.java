package laf.core.web.resource;

public interface Resource {
	String getName();

	byte[] getData();

	DataEqualityTracker getDataEqualityTracker();

	default boolean containsSameDataAs(Resource other) {
		return getDataEqualityTracker().containsSameDataAs(
				other.getDataEqualityTracker());

	}
}
