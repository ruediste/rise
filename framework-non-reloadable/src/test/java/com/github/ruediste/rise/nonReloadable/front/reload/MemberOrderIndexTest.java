package com.github.ruediste.rise.nonReloadable.front.reload;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.Matcher;
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
            Pair<ClassNode, List<String>> pair = AsmUtil
                    .readClassWithMembers(cls);
            trx.addedClasses.add(pair.getA());
            trx.addedClassesMembers.put(pair.getA().name, pair.getB());
        }
        index.onChange(trx);
    }

    @Before
    public void before() throws IOException {
        readClasses(Base.class, Derived2.class, Derived1.class);
    }

    @Test
    public void testOrderMembers() throws Exception {
        checkOrder(Derived2.class, new String[] { "foo", "bar", "foobar" },
                false);
        checkOrder(Derived1.class, new String[] { "bar1", "bar" }, false);
        checkOrder(Base.class, new String[] { "foobar", "foo" }, false);
    }

    private void checkOrder(Class<?> cls, String[] names, boolean checkNoOrder) {
        checkOrder(cls, names, checkNoOrder, Arrays.asList(cls.getMethods()));
        checkOrder(cls, names, checkNoOrder, Arrays.asList(cls.getFields()));
    }

    private void checkOrder(Class<?> cls, String[] names, boolean checkNoOrder,
            List<Member> members) {
        List<Member> filteredMembers = members.stream()
                .filter(x -> cls.equals(x.getDeclaringClass()))
                .collect(toList());

        List<Member> ordered = index.orderMembers(cls, filteredMembers);

        checkOrder(ordered, contains(names));
        if (checkNoOrder)
            checkOrder(filteredMembers, not(contains(names)));
    }

    public void checkOrder(Collection<Member> members,
            Matcher<Iterable<? extends String>> matcher) {

        assertThat(members.stream().map(x -> x.getName()).collect(toList()),
                matcher);
    }
}
