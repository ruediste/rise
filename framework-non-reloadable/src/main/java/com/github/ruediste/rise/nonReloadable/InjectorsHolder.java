package com.github.ruediste.rise.nonReloadable;

import com.github.ruediste.salta.jsr330.Injector;

public class InjectorsHolder {
    public static class Holder {
        Injector nonRestartableInjector;
        Injector restartableInjector;

        Holder(Injector nonRestartableInjector, Injector restartableInjector) {
            super();
            this.nonRestartableInjector = nonRestartableInjector;
            this.restartableInjector = restartableInjector;
        }

    }

    static ThreadLocal<Holder> holder = new ThreadLocal<>();

    private InjectorsHolder() {
    }

    public static boolean injectorsPresent() {
        return holder.get() != null;
    }

    public static Injector getRestartableInjector() {
        return holder.get().restartableInjector;
    }

    public static Injector getNonRestartableInjector() {
        return holder.get().nonRestartableInjector;
    }

    public static void withInjectors(Injector nonRestartableInjector,
            Injector restartableInjector, Runnable run) {
        Holder old = setInjectors(nonRestartableInjector, restartableInjector);
        try {
            run.run();
        } finally {
            restoreInjectors(old);
        }
    }

    public static void restoreInjectors(Holder old) {
        if (old == null)
            holder.remove();
        else
            holder.set(old);
    }

    public static Holder setInjectors(Injector nonRestartableInjector,
            Injector restartableInjector) {
        Holder old = holder.get();
        holder.set(new Holder(nonRestartableInjector, restartableInjector));
        return old;
    }
}
