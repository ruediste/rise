package laf.initialization;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import javax.inject.Singleton;

@Singleton
public class InitializationService {

	private static class MethodInitializer extends InitializerImpl {

		ArrayList<InitializerMatcher> beforeMatchers = new ArrayList<>();
		ArrayList<InitializerMatcher> afterMatchers = new ArrayList<>();
		private Object component;
		private Method method;

		public MethodInitializer(Method method, Object component,
				LafInitializer lafInitializer) {
			super(component.getClass());
			this.method = method;
			this.component = component;

			for (Class<?> cls : lafInitializer.before()) {
				beforeMatchers.add(new InitializerMatcher(cls));
			}
			for (Class<?> cls : lafInitializer.after()) {
				afterMatchers.add(new InitializerMatcher(cls));
			}
		}

		@Override
		public boolean declaresIsBefore(Initializer other) {
			for (InitializerMatcher m : beforeMatchers) {
				if (m.matches(other)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean declaresIsAfter(Initializer other) {
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
			result.addAll(createInitializers(component));
		}
		return result;
	}

	public boolean mightCreateInitializers(Class<?> clazz) {
		if (InitializerProvider.class.isAssignableFrom(clazz)) {
			return true;
		}
		for (Method method : clazz.getMethods()) {
			LafInitializer lafInitializer = method
					.getAnnotation(LafInitializer.class);
			if (lafInitializer != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Scan the provided object for a single method annotated with
	 * {@link LafInitializer} and create an {@link Initializer}, which will call
	 * this method. The dependencies are declared with the annotation. If no
	 * initializer method is found, null is returned.
	 */
	public Collection<Initializer> createInitializers(Object object) {
		ArrayList<Initializer> result = new ArrayList<>();

		// handle InitializerProvider
		if (object instanceof InitializerProvider) {
			for (Initializer i : ((InitializerProvider) object)
					.getInitializers()) {
				result.add(i);
			}
		}

		Class<? extends Object> representingClass = object.getClass();
		for (Method method : representingClass.getMethods()) {
			LafInitializer lafInitializer = method
					.getAnnotation(LafInitializer.class);
			if (lafInitializer == null) {
				continue;
			}
			result.add(new MethodInitializer(method, object, lafInitializer));
		}
		return result;
	}

	public void runInitializers(Iterable<Initializer> initializers,
			Initializer rootInitializer) {
		// check for duplicates and the uniqueness of ids
		if (!checkUnique(initializers)) {
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

	void initialize(Class<?> rootInitializerRepresentingClass) {

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
