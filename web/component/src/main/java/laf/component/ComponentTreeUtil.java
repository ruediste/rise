package laf.component;

import java.util.*;

/**
 * Static utility class for component tree traversal.
 *
 */
public class ComponentTreeUtil {

	/**
	 * Return all components in the sub tree rooted in the given component. That
	 * is the component itself and all children, transitively.
	 */
	static public List<Component> subTree(Component c) {
		ArrayList<Component> result = new ArrayList<>();
		subTree(c, result);
		return result;
	}

	static private void subTree(Component c, ArrayList<Component> result) {
		result.add(c);
		for (Component child : c.getChildren()) {
			subTree(child, result);
		}
	}

	/**
	 * Returns the ancestors of the given {@link Component}, starting with the
	 * parent of the component and ending with the root component.
	 */
	static public List<Component> ancestors(Component start) {
		ArrayList<Component> result = new ArrayList<>();
		Component c = start.getParent();
		while (c != null) {
			result.add(c);
			c = c.getParent();
		}
		return result;
	}

	/**
	 * Returns the path from the root component to the target component, not
	 * including the target component.
	 */
	static public Collection<Component> path(Component target) {
		List<Component> result = ancestors(target);
		Collections.reverse(result);
		return result;
	}
}
