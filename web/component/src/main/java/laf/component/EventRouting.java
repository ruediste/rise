package laf.component;

import java.util.Collections;

public enum EventRouting {
	/**
	 * The event is delivered directly to the target
	 */
	DIRECT {
		@Override
		public Iterable<Component> getCandidateComponents(
				Component startComponent) {
			return Collections.singletonList(startComponent);
		}
	},

	/**
	 * The event bubbles up the hierarchy along the parent links
	 */
	BUBBLE {
		@Override
		public Iterable<Component> getCandidateComponents(
				Component startComponent) {
			return ComponentTreeUtil.ancestors(startComponent, true);
		}
	},

	/**
	 * The event tunnels down towards the target. The same {@link Component}s
	 * are visited as with {@link #BUBBLE} routing, but in the reverse order.
	 */
	TUNNEL {
		@Override
		public Iterable<Component> getCandidateComponents(
				Component startComponent) {
			return ComponentTreeUtil.path(startComponent, true);
		}
	},

	/**
	 * The event is sent to the target and to all it's children, transitively.
	 */
	BROADCAST {
		@Override
		public Iterable<Component> getCandidateComponents(
				Component startComponent) {
			return ComponentTreeUtil.subTree(startComponent);
		}
	};

	abstract public Iterable<Component> getCandidateComponents(
			Component startComponent);
}
