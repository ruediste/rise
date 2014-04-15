package laf.initialization;

import java.util.*;

public class CreateInitializersEvent {
	final Set<Initializer> initializers = new HashSet<>();

	public void addInitializer(Initializer initializer) {
		initializers.add(initializer);
	}

	public void addInitializers(Collection<Initializer> initializers) {
		this.initializers.addAll(initializers);
	}
}
