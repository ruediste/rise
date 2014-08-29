package laf.mvc.core.actionPath;

/**
 * Represent string values attached to {@link ActionPath}s. These values are
 * transferred along with the ActionPath to the client and back again.
 */
public class ActionPathParameter {

	private final String name;

	public ActionPathParameter(String name) {
		this.name = name;

	}

	public void set(ActionPath<?> path, String value) {
		path.parameters.put(this, value);
	}

	public String get(ActionPath<?> path) {
		return path.parameters.get(this);
	}

	public String getName() {
		return name;
	}
}
