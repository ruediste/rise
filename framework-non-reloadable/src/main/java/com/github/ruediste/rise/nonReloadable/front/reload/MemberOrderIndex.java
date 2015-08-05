package com.github.ruediste.rise.nonReloadable.front.reload;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.Type;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.google.common.collect.Iterables;

@Singleton
@NonRestartable
public class MemberOrderIndex {

    @Inject
    ClassChangeNotifier notifier;

    @Inject
    Logger log;

    public void setup() {
        notifier.addPreListener(this::onChange);
    }

    Map<String, Map<String, Integer>> classes = new HashMap<>();

    void onChange(ClassChangeTransaction trx) {
        log.info("change occurred. added:" + trx.addedClasses.size()
                + " removed:" + trx.removedClasses.size() + " modified:"
                + trx.modifiedClasses.size());
        Stream.concat(trx.removedClasses.stream(),
                trx.modifiedClasses.stream().map(n -> n.name)).forEach(
                classes::remove);

        for (Entry<String, List<String>> entry : Iterables.concat(
                trx.modifiedClassesMembers.entrySet(),
                trx.addedClassesMembers.entrySet())) {
            String name = entry.getKey();
            List<String> members = entry.getValue();

            log.trace("Registring class {}", name);
            Map<String, Integer> map = new HashMap<>();
            int i = 0;
            for (String member : members) {
                map.put(member, i++);
            }
            classes.put(name, map);
        }
    }

    private String memberString(Member member) {
        if (member instanceof Field) {
            return "F" + member.getName() + ";"
                    + Type.getDescriptor(((Field) member).getType());
        } else if (member instanceof Constructor<?>) {
            return "M" + member.getName() + ";"
                    + Type.getConstructorDescriptor((Constructor<?>) member);
        } else if (member instanceof Method) {
            return "M" + member.getName() + ";"
                    + Type.getMethodDescriptor((Method) member);
        } else
            throw new RuntimeException("Unknown member type " + member);
    }

    /**
     * Order members which are all declared in the same class by their
     * appearance in the class file (which should match source code order)
     */
    public List<Member> orderMembers(Class<?> declaringClass,
            Collection<? extends Member> members) {

        Map<String, Integer> map = classes.get(Type
                .getInternalName(declaringClass));
        if (map == null)
            throw new RuntimeException("Unknown class " + declaringClass);
        return members
                .stream()
                .sorted((a, b) -> {
                    Integer idxA = map.get(memberString(a));
                    if (idxA == null) {
                        throw new RuntimeException("Member " + a
                                + "is not declared in " + declaringClass);
                    }
                    Integer idxB = map.get(memberString(b));
                    if (idxB == null) {
                        throw new RuntimeException("Member " + b
                                + "is not declared in " + declaringClass);
                    }
                    return idxA.compareTo(idxB);
                }).collect(toList());
    }
}
