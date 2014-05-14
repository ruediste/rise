package laf.component;

import java.util.Collections;

import org.apache.myfaces.shared.util.ComponentUtils;

import com.google.common.collect.Iterables;

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
			return Iterables.concat(Collections.singleton(startComponent),ComponentTreeUtil.ancestors(startComponent);
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
			// TODO Auto-generated method stub
			return null;
		}
	},

	/**
	 * The event is sent to the target and to all it's children, transitively.
	 */
	BROADCAST {
		@Override
		public Iterable<Component> getCandidateComponents(
				Component startComponent) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	abstract public Iterable<Component> getCandidateComponents(
			Component startComponent);
}
