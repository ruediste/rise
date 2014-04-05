package laf.initializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class InitializationEngine {

	private static class MethodInitializer extends InitializerImpl {

		ArrayList<InitializerMatcher> beforeMatchers = new ArrayList<>();
		ArrayList<InitializerMatcher> afterMatchers = new ArrayList<>();
		private Object component;
		private Method method;

		public MethodInitializer(Method method, Object component,
				LafInitializer lafInitializer) {
			super(component.getClass(), method.getName());
			this.method = method;
			this.component = component;

			for (Class<?> cls : lafInitializer.before()) {
				beforeMatchers.add(new InitializerMatcher(cls));
			}
			for (Class<?> cls : lafInitializer.after()) {
				afterMatchers.add(new InitializerMatcher(cls));
			}

			for (InitializerRef ref : lafInitializer.beforeRef()) {
				beforeMatchers.add(new InitializerMatcher(ref));
			}
			for (InitializerRef ref : lafInitializer.afterRef()) {
				afterMatchers.add(new InitializerMatcher(ref));
			}
		}

		@Override
		public boolean isBefore(Initializer other) {
			for (InitializerMatcher m : beforeMatchers) {
				if (m.matches(other)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean isAfter(Initializer other) {
			for (InitializerMatcher m : afterMatchers) {
				if (m.matches(other)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void run() {
			try {
				method.invoke(component);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new RuntimeException(
						"Unable to invoke initializer method " + method);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Error in initializer method "
						+ method, e.getCause());
			}
		}

		@Override
		public String toString() {
			return method.toString();
		}

	}

	public Collection<Initializer> createInitializersFromComponents(
			Iterable<?> components) {
		ArrayList<Initializer> result = new ArrayList<>();
		for (Object component : components) {
			result.addAll(createInitializersFromComponent(component));
		}
		return result;
	}

	public Collection<Initializer> createInitializersFromComponent(
			Object component) {
		ArrayList<Initializer> result = new ArrayList<>();

		// handle InitializerProvider
		if (component instanceof InitializerProvider) {
			for (Initializer i : ((InitializerProvider) component)
					.getInitializers()) {
				result.add(i);
			}
		}

		Class<? extends Object> componentClass = component.getClass();
		for (Method method : componentClass.getMethods()) {
			LafInitializer lafInitializer = method
					.getAnnotation(LafInitializer.class);
			if (lafInitializer == null) {
				continue;
			}
			result.add(new MethodInitializer(method, component, lafInitializer));
		}
		return result;
	}

	public void runInitializers(Iterable<Initializer> initializers) {
		// check for duplicates and the uniqueness of ids
		if (!checkUnique(initializers)) {
			throw new RuntimeException("duplication initializer detected");
		}

		if (!checkUniqueIds(initializers)) {
			throw new RuntimeException("duplication initializer detected");
		}

		// determine before relation
		Map<Initializer, Set<Initializer>> before = calculateBeforeRelation(initializers);

		// initialize map containing after how many initializers a given
		// initializer can be run, as well as remaining
		Map<Initializer, Integer> afterCount = new LinkedHashMap<>();
		HashSet<Initializer> remaining = new LinkedHashSet<>();
		for (Initializer i : initializers) {
			afterCount.put(i, 0);
			remaining.add(i);
		}

		for (Set<Initializer> set : before.values()) {
			for (Initializer i : set) {
				afterCount.put(i, afterCount.get(i) + 1);
			}
		}

		// move initializers with afterCount = 0 from remaining to runnable
		LinkedHashSet<Initializer> runnable = new LinkedHashSet<>();
		for (Initializer i : initializers) {
			if (afterCount.get(i).equals(0)) {
				remaining.remove(i);
				runnable.add(i);
			}
		}

		// run initializers
		while (!runnable.isEmpty()) {
			Initializer i = runnable.iterator().next();
			runnable.remove(i);
			for (Initializer p : before.get(i)) {
				int newCount = afterCount.get(p) - 1;
				afterCount.put(p, newCount);
				if (newCount == 0) {
					remaining.remove(p);
					runnable.add(p);
				}
			}

			// run initializer
			i.run();
		}

		if (!remaining.isEmpty()) {
			throw new RuntimeException(
					"There was a loop in the initializer dependencies. Remaining Initializers: "
							+ initializers + " Dependencies:" + before);
		}
	}

	Map<Initializer, Set<Initializer>> calculateBeforeRelation(
			Iterable<Initializer> initializers) {
		HashMap<Initializer, Set<Initializer>> result = new LinkedHashMap<>();

		for (Initializer i : initializers) {
			result.put(i, new LinkedHashSet<Initializer>());
		}

		for (Initializer i : initializers) {
			for (Initializer p : initializers) {
				if (i == p) {
					continue;
				}

				if (i.isAfter(p)) {
					result.get(p).add(i);
				}

				if (i.isBefore(p)) {
					result.get(i).add(p);
				}
			}
		}
		return result;
	}

	boolean checkUniqueIds(Iterable<Initializer> initializers) {
		HashMap<Class<?>, HashSet<String>> map = new HashMap<>();
		for (Initializer i : initializers) {
			HashSet<String> set = map.get(i.getComponentClass());
			if (set == null) {
				set = new HashSet<>();
				map.put(i.getComponentClass(), set);
			}

			if (!set.add(i.getId())) {
				return false;
			}
		}
		return true;
	}

	boolean checkUnique(Iterable<Initializer> initializers) {
		HashSet<Initializer> initializerSet = new HashSet<>();
		for (Initializer init : initializers) {
			if (!initializerSet.add(init)) {
				return false;
			}
		}
		return true;
	}

}
