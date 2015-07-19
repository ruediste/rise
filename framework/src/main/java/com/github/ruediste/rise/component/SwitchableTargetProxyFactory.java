package com.github.ruediste.rise.component;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.ProxyRefDispatcher;

/**
 * Factory for proxies delegating to a default target. The target can be
 * switched afterwards on a per-thread basis
 */
public class SwitchableTargetProxyFactory {

    private static final class Dispatcher implements ProxyRefDispatcher {

        ThreadLocal<Object> target = new ThreadLocal<>();
        private Object defaultTarget;

        public Dispatcher(Object defaultTarget) {
            this.defaultTarget = defaultTarget;
        }

        @Override
        public Object loadObject(Object proxy) throws Exception {
            Object result = target.get();
            if (result == null)
                return defaultTarget;
            else
                return result;
        }

        public void withTarget(Object newTarget, Runnable run) {
            Object old = this.target.get();
            this.target.set(newTarget);
            try {
                run.run();
            } finally {
                if (old == null)
                    target.remove();
                else
                    this.target.set(old);
            }
        }
    }

    /**
     * Switch the target of the given proxy (created via
     * {@link SwitchableTargetProxyFactory#createProxy(Object)}) to the new
     * target for the execution of the given runnable (on this thread only)
     */
    static public <T> void withTarget(T proxy, T newTarget, Runnable run) {
        Factory factory = (Factory) proxy;
        Dispatcher dispatcher = (Dispatcher) factory.getCallback(0);
        dispatcher.withTarget(newTarget, run);
    }

    /**
     * Create a proxy. The target can be switched afterwards using
     * {@link SwitchableTargetProxyFactory#withTarget(Object, Object, Runnable)}
     */
    @SuppressWarnings("unchecked")
    static public <T> T createProxy(T target) {
        Enhancer e = new Enhancer();
        e.setSuperclass(target.getClass());
        e.setCallback(new Dispatcher(target));
        return (T) e.create();
    }
}
