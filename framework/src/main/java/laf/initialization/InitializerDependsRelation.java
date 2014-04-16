package laf.initialization;

import java.util.Objects;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof InitializerDependsRelation)) {
			return false;
		}
		InitializerDependsRelation other = (InitializerDependsRelation) obj;
		return Objects.equals(source, other.getSource())
				&& Objects.equals(target, other.getTarget())
				&& Objects.equals(isOptional, other.isOptional());
	}
}
