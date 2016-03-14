package com.github.ruediste.rise.nonReloadable.front.reload;

import static java.util.stream.Collectors.toSet;
import static org.objectweb.asm.Opcodes.ASM5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;

import com.github.ruediste.rise.nonReloadable.NonRestartable;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;

/**
 * An index containing the whole class hierarchy as well as the parsed classes.
 */
@Singleton
@NonRestartable
public class ClassHierarchyIndex {
    private final class SignatureVisitorImpl extends SignatureVisitor {
        public Map<String, String> parameterMap = new HashMap<>();
        private List<String> parameterValuesIn;
        int currentParameter;
        public ArrayList<String> parameterValuesOut = new ArrayList<>();

        private SignatureVisitorImpl(List<String> parameterValuesIn) {
            super(ASM5);
            this.parameterValuesIn = parameterValuesIn;
        }

        @Override
        public void visitFormalTypeParameter(String name) {
            if (currentParameter < parameterValuesIn.size())
                parameterMap.put(name, parameterValuesIn.get(currentParameter++));
        }

        @Override
        public SignatureVisitor visitSuperclass() {
            return new SignatureVisitor(ASM5) {
                @Override
                public SignatureVisitor visitTypeArgument(char wildcard) {
                    return new SignatureVisitor(ASM5) {
                        @Override
                        public void visitTypeVariable(String name) {
                            parameterValuesOut.add(parameterMap.get(name));
                        }

                        @Override
                        public void visitClassType(String name) {
                            parameterValuesOut.add(name);
                        }
                    };
                }
            };
        }
    }

    @Inject
    ClassChangeNotifier notifier;

    @Inject
    Logger log;

    public void setup() {
        notifier.addPreListener(this::onChange);
    }

    private Map<String, ClassNode> classMap = new HashMap<>();
    private HashMultimap<String, ClassNode> childMap = HashMultimap.create();

    void onChange(ClassChangeTransaction trx) {
        log.info("change occurred. added:" + trx.addedClasses.size() + " removed:" + trx.removedClasses.size()
                + " modified:" + trx.modifiedClasses.size());
        Stream.concat(trx.removedClasses.stream(), trx.modifiedClasses.stream().map(n -> n.name)).forEach(name -> {
            ClassNode cls = classMap.remove(name);
            if (cls != null) {
                if (cls.superName != null) {
                    childMap.remove(cls.superName, cls);
                }
                if (cls.interfaces != null) {
                    for (String iface : cls.interfaces) {
                        childMap.remove(iface, cls);
                    }
                }
            }
        });

        for (ClassNode cls : Iterables.concat(trx.modifiedClasses, trx.addedClasses)) {
            log.trace("Registring class {}", cls.name);
            classMap.put(cls.name, cls);
            if (cls.superName != null) {
                childMap.put(cls.superName, cls);
            }
            if (cls.interfaces != null) {
                for (String iface : cls.interfaces) {
                    childMap.put(iface, cls);
                }
            }
        }
    }

    /**
     * Return the parsed class node for the given internal name
     */
    public Optional<ClassNode> tryGetNode(String internalName) {
        return Optional.ofNullable(classMap.get(internalName));
    }

    /**
     * Return the parsed class node for the given internal name
     */
    public ClassNode getNode(String internalName) {
        ClassNode result = classMap.get(internalName);
        if (result == null)
            throw new RuntimeException("Class " + internalName + " not in index");
        return result;
    }

    /**
     * Return the child classes for the given class or interface given by it's
     * internal name
     */
    public Set<ClassNode> getChildren(String internalName) {
        Set<ClassNode> result = childMap.get(internalName);
        if (result == null)
            return Collections.emptySet();
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> Set<Class<? extends T>> getAllChildClasses(Class<T> cls, ClassLoader cl) {
        return getAllChildren(cls).stream().map(n -> {
            try {
                return (Class<? extends T>) cl.loadClass(Type.getObjectType(n.name).getClassName());
            } catch (Exception e) {
                throw new RuntimeException("Error while loading class " + n.name);
            }
        }).collect(toSet());
    }

    public Set<ClassNode> getAllChildren(Class<?> cls) {
        return getAllChildren(Type.getInternalName(cls));
    }

    public Set<ClassNode> getAllChildren(String internalName) {
        HashSet<ClassNode> result = new HashSet<>();
        getAllChildren(internalName, result);
        return result;
    }

    private void getAllChildren(String internalParentName, HashSet<ClassNode> result) {
        for (ClassNode child : getChildren(internalParentName)) {
            if (result.add(child)) {
                getAllChildren(child.name, result);
            }
        }
    }

    /**
     * Checks if a type is assignable from another
     */
    public boolean isAssignableFrom(String internalNameParent, String internalName) {
        HashSet<String> seen = new HashSet<>();
        return isAssignableFrom(internalNameParent, internalName, seen);
    }

    private boolean isAssignableFrom(String internalNameParent, String internalName, HashSet<String> seen) {
        log.trace("isAssignableFrom " + internalNameParent + " " + internalName);
        if (Objects.equals(internalName, internalNameParent))
            return true;

        if (internalName == null)
            return false;

        if (!seen.add(internalName))
            return false;

        ClassNode node;
        {
            Optional<ClassNode> tmp = tryGetNode(internalName);
            if (!tmp.isPresent())
                return false;
            node = tmp.get();
        }

        if (isAssignableFrom(internalNameParent, node.superName, seen))
            return true;

        if (node.interfaces != null)
            for (String iface : node.interfaces) {
                if (isAssignableFrom(internalNameParent, iface, seen))
                    return true;
            }
        return false;
    }

    public Collection<ClassNode> getAllNodes() {
        return classMap.values();
    }

    public ClassNode getNode(Class<?> cls) {
        return getNode(Type.getInternalName(cls));
    }

    public String resolve(String childClass, String baseClass, String typeVariable) {
        return resolve(getNode(childClass), baseClass, typeVariable);
    }

    public String resolve(ClassNode view, Class<?> clazz, String typeVariable) {
        return resolve(view, Type.getInternalName(clazz), typeVariable);
    }

    /**
     * Resolve the given type variable in the base class, as it is parameterized
     * by the childClass
     */
    public String resolve(ClassNode child, String baseClass, String typeVariable) {
        ArrayList<String> parameterValues = new ArrayList<>();

        while (true) {
            // System.out.println(child.signature);
            // new SignatureReader(child.signature)
            // .accept(new PrintingSignatureVisitor(""));

            SignatureVisitorImpl visitor = new SignatureVisitorImpl(parameterValues);
            if (child.signature != null) {
                new SignatureReader(child.signature).accept(visitor);
                parameterValues = visitor.parameterValuesOut;
            }

            if (child.name.equals(baseClass)) {
                return visitor.parameterMap.get(typeVariable);
            }
            if (child.superName == null)
                break;
            child = getNode(child.superName);
        }
        return null;
    }

}
