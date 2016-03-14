package com.github.ruediste.rise.nonReloadable.front.reload;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.github.ruediste.rise.util.AsmUtil;
import com.github.ruediste.rise.util.Pair;

@RunWith(MockitoJUnitRunner.class)
public class MemberOrderIndexTest {

    @Mock
    Logger log;

    @InjectMocks
    MemberOrderIndex index;

    @SuppressWarnings("unused")
    private static class Base {
        public int foobar;

        public void foobar() {
        }

        public int foo;

        public void foo() {
        }

    }

    @SuppressWarnings("unused")
    private static class Derived1 extends Base {

        public int bar1;

        public void bar1() {
        }

        public int bar;

        public void bar() {
        }
    }

    @SuppressWarnings("unused")
    private static class Derived2 extends Derived1 {

        public int foo;

        @Override
        public void foo() {
        }

        public int bar;

        @Override
        public void bar() {
        }

        public int foobar;

        @Override
        public void foobar() {
        }
    }

    private void readClasses(Class<?>... classes) {
        ClassChangeTransaction trx = new ClassChangeNotifier.ClassChangeTransaction();
        for (Class<?> cls : classes) {
            Pair<ClassNode, List<String>> pair = AsmUtil.readClassWithMembers(cls);
            trx.addedClasses.add(pair.getA());
            trx.addedClassesMembers.put(pair.getA().name, pair.getB());
        }
        index.onChange(trx);
    }

    @Before
    public void before() throws IOException {
        readClasses(Base.class, Derived2.class, Derived1.class, Object.class);
    }

    @Test
    public void testOrderMembers() throws Exception {
        checkOrder(Derived2.class, names("foo", "bar", "foobar"), Derived2.class.getDeclaredMethods());
        checkOrder(Derived2.class, names("foo", "bar", "foobar", "foo", "bar", "foobar"),
                Derived2.class.getDeclaredFields(), Derived2.class.getDeclaredMethods());
        checkOrder(Derived2.class, names("bar1", "bar", "foo", "bar", "foobar"), Derived2.class.getDeclaredFields(),
                Derived1.class.getDeclaredFields());
        checkOrder(Derived2.class, names("foobar", "foo", "bar1", "bar", "foo", "bar", "foobar"),
                Derived2.class.getDeclaredFields(), Derived1.class.getDeclaredFields(), Base.class.getDeclaredFields());
        checkOrder(Derived2.class, names("foobar", "foo", "bar1", "bar", "foo", "bar", "foobar"),
                Derived2.class.getDeclaredMethods(), Derived1.class.getDeclaredMethods(),
                Base.class.getDeclaredMethods());
    }

    private String[] names(String... names) {
        return names;
    }

    private void checkOrder(Class<?> cls, String[] names, Member[]... members) {

        List<Member> orderedMembers = index.orderMembers(cls,
                Arrays.stream(members).flatMap(Arrays::stream).collect(toList()));

        List<String> memberNames = orderedMembers.stream().map(Member::getName).collect(toList());
        assertThat(
                "\n" + orderedMembers.stream()
                        .map(x -> x.getClass().getSimpleName() + " " + x.getDeclaringClass().getSimpleName() + "."
                                + x.getName())
                        .collect(joining(", ")) + "\n" + memberNames,
                memberNames, contains(names));
    }
}
