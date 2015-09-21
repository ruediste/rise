package com.github.ruediste.rise.nonReloadable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Takes a {@link Throwable} and removes stack frames which do not satisfy a
 * certain {@link Predicate}, if more than three of them are found in a row. The
 * first and the last frame are kept, and in the middle a stack frame indicating
 * the number of omissed frames is added.
 * 
 */
public class DefaultStrackTraceFilter implements StackTraceFilter {

    private Predicate<StackTraceElement> shouldInclude;

    public DefaultStrackTraceFilter(
            Predicate<StackTraceElement> shouldInclude) {
        this.shouldInclude = shouldInclude;
    }

    @Override
    public void filter(Throwable input) {
        IdentityHashMap<Throwable, Throwable> seen = new IdentityHashMap<>();
        filterImpl(input, seen);
    }

    private void filterImpl(Throwable input, Map<Throwable, Throwable> seen) {
        if (input == null)
            return;
        if (seen.put(input, input) != null)
            return;
        filterImpl(input.getCause(), seen);

        StackTraceElement[] inputTraces = input.getStackTrace();
        ArrayList<StackTraceElement> outputTraces = new ArrayList<>();

        ArrayList<StackTraceElement> fragment = new ArrayList<>();
        for (StackTraceElement inputTrace : inputTraces) {
            if (shouldInclude.test(inputTrace)) {
                outputTraces.addAll(reduceFragment(fragment));
                outputTraces.add(inputTrace);
            } else {
                fragment.add(inputTrace);
            }
        }
        outputTraces.addAll(reduceFragment(fragment));
        input.setStackTrace(outputTraces.toArray(new StackTraceElement[] {}));
    }

    private Collection<? extends StackTraceElement> reduceFragment(
            List<StackTraceElement> fragment) {
        ArrayList<StackTraceElement> result = new ArrayList<>();
        if (fragment.size() == 0) {
            // NOP
        } else if (fragment.size() <= 3) {
            result.addAll(fragment);
        } else {
            StackTraceElement first = fragment.get(0);
            StackTraceElement last = fragment.get(fragment.size() - 1);
            result.add(first);
            int lineLength = (first.getClassName().length()
                    + last.getClassName().length()) / 2;
            String className = String.format("%" + lineLength + "s", "")
                    .replace(' ', '-');
            result.add(new StackTraceElement(className,
                    "omitting " + (fragment.size() - 2) + " stack frames", null,
                    0));
            result.add(last);
        }
        fragment.clear();
        return result;
    }
}
