package laf.initializer;

import java.util.*;

public class InitializationEngine {

	Iterable<Initializer> createInitializersFromComponent(Object component) {
		return null;
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
