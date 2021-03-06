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

import com.github.ruediste.c3java.linearization.JavaC3;
import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

/**
 * Index keeping track of the order of the member of all classes. Please note
 * that fields are always ordered before methods.
 */
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
        log.info("change occurred. added:" + trx.addedClassesMembers.size() + " removed:" + trx.removedClasses.size()
                + " modified:" + trx.modifiedClassesMembers.size());
        Stream.concat(trx.removedClasses.stream(), trx.modifiedClasses.stream().map(n -> n.name))
                .forEach(classes::remove);

        for (Entry<String, List<String>> entry : Iterables.concat(trx.modifiedClassesMembers.entrySet(),
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
            return "F" + member.getName() + ";" + Type.getDescriptor(((Field) member).getType());
        } else if (member instanceof Constructor<?>) {
            return "M" + member.getName() + ";" + Type.getConstructorDescriptor((Constructor<?>) member);
        } else if (member instanceof Method) {
            return "M" + member.getName() + ";" + Type.getMethodDescriptor((Method) member);
        } else
            throw new RuntimeException("Unknown member type " + member);
    }

    /**
     * Order members which are all declared in the same class by their
     * appearance in the class file (which should match source code order)
     */
    public <T extends Member> List<T> orderMembers(Class<?> leafClass, Collection<T> members) {
        // group members by declaring class
        Multimap<Class<?>, T> memberMap = MultimapBuilder.hashKeys().arrayListValues().build();
        for (T member : members) {
            memberMap.put(member.getDeclaringClass(), member);
        }

        return JavaC3.allSuperclassesReverse(leafClass).stream().flatMap(cls -> {
            Collection<T> clsMembers = memberMap.get(cls);
            if (clsMembers == null)
                return Stream.empty();
            Map<String, Integer> map = classes.get(Type.getInternalName(cls));
            if (map == null)
                throw new RuntimeException("type " + cls + " not found in members order index. Did it get scanned?");
            return clsMembers.stream().sorted((a, b) -> {
                Integer idxA = map.get(memberString(a));
                if (idxA == null) {
                    throw new RuntimeException("Member " + a + " is not declared in " + cls);
                }
                Integer idxB = map.get(memberString(b));
                if (idxB == null) {
                    throw new RuntimeException("Member " + b + " is not declared in " + cls);
                }
                return idxA.compareTo(idxB);
            });
        }).collect(toList());

    }
}
