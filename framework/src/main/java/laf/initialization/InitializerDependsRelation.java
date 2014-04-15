package laf.initialization;

/**
 * Represents a depends relation between two initializers
 */
public class InitializerDependsRelation {

	private final Initializer source;
	private final Initializer target;
	private final boolean isOptional;

	public InitializerDependsRelation(Initializer source, Initializer target,
			boolean isOptional) {
		super();
		this.source = source;
		this.target = target;
		this.isOptional = isOptional;
	}

	public Initializer getSource() {
		return source;
	}

	public Initializer getTarget() {
		return target;
	}

	public boolean isOptional() {
		return isOptional;
	};
}
