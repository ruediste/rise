package com.github.ruediste.rise.core.security.authorization;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;

public class MethodImplementationFinderTest {

    interface IBase {
        void ib_b();

        default void def_ib() {

        }

        default void def_ib_d() {

        }
    }

    @SuppressWarnings("unused")
    class Base implements IBase {
        public void p_b() {
        }

        public void p_b_d() {
        }

        @Override
        public void ib_b() {
        }

        private void pr_b_d() {
        }
    }

    interface IDerived extends IBase {
        public void id_d();
    }

    @SuppressWarnings("unused")
    class Derived extends Base implements IDerived {

        public void p_d() {
        }

        @Override
        public void id_d() {
        }

        @Override
        public void p_b_d() {
        }

        @Override
        public void def_ib_d() {
        }

        private void pr_b_d() {
        }
    }

    private CalledOn when(Class<?> cls, String name) {
        return when(method(cls, name));
    }

    private CalledOn when(Method calledMethod) {
        return new CalledOn(calledMethod);
    }

    private class CalledOn {
        private Method calledMethod;

        public CalledOn(Method calledMethod) {
            this.calledMethod = calledMethod;
        }

        Expect calledOn(Class<?> instanceClass) {
            return new Expect(calledMethod, instanceClass);
        }
    }

    private class Expect {
        private Method calledMethod;
        private Class<?> instanceClass;

        public Expect(Method calledMethod, Class<?> instanceClass) {
            this.calledMethod = calledMethod;
            this.instanceClass = instanceClass;
        }

        public void expectSelf() {
            assertEquals(calledMethod,
                    MethodImplementationFinder.findImplementation(
                            instanceClass, calledMethod));
        }

        public void expect(Class<?> cls, String name) {
            assertEquals(method(cls, name),
                    MethodImplementationFinder.findImplementation(
                            instanceClass, calledMethod));
        }

        public void expect(Class<Derived> cls) {
            expect(cls, calledMethod.getName());
        }

        public void expectOnInstance() {
            expect(instanceClass, calledMethod.getName());
        }
    }

    @Test
    public void testPrivate() {
        when(Base.class, "pr_b_d").calledOn(Base.class).expectSelf();
        when(Derived.class, "pr_b_d").calledOn(Derived.class).expectSelf();
        when(Base.class, "pr_b_d").calledOn(Derived.class).expectSelf();
    }

    @Test
    public void testPublic() {
        when(Base.class, "p_b").calledOn(Base.class).expectSelf();
        when(Derived.class, "p_d").calledOn(Derived.class).expectSelf();

        when(Base.class, "p_b_d").calledOn(Base.class).expectSelf();
        when(Derived.class, "p_b_d").calledOn(Derived.class).expectSelf();

        when(Base.class, "p_b_d").calledOn(Derived.class).expectOnInstance();
    }

    @Test
    public void testDefault() throws Exception {
        when(IBase.class, "def_ib").calledOn(Base.class).expectSelf();
        when(IBase.class, "def_ib").calledOn(Derived.class).expectSelf();
        when(Derived.class.getMethod("def_ib")).calledOn(Derived.class).expect(
                IBase.class, "def_ib");
        when(IBase.class, "def_ib_d").calledOn(Base.class).expectSelf();
        when(IBase.class, "def_ib_d").calledOn(Derived.class)
                .expectOnInstance();
    }

    private Method method(Class<?> cls, String name) {
        Method m;
        try {
            m = cls.getDeclaredMethod(name);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        return m;
    }
}
