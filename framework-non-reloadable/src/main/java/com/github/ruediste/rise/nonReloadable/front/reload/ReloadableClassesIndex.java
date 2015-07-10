package com.github.ruediste.rise.nonReloadable.front.reload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;

/**
 * Information cache to determine if a class is reloadable or not
 */
@Singleton
public class ReloadableClassesIndex {
    @Inject
    ClassChangeNotifier notifier;

    @PostConstruct
    public void setup() {
        notifier.addListener(this::onChange);
    }

    private Map<String, String> outerClassNames = new HashMap<>();
    Map<String, Boolean> isReloadableMap = new HashMap<>();

    void onChange(ClassChangeTransaction trx) {
        for (String name : trx.removedClasses) {
            isReloadableMap.remove(name);
            outerClassNames.remove(name);
        }

        updateMap(trx.addedClasses);
        updateMap(trx.modifiedClasses);
    }

    private void updateMap(Set<ClassNode> classes) {
        for (ClassNode cls : classes) {
            updateMap(cls);
        }
    }

    void updateMap(ClassNode cls) {
        if (cls.outerClass != null) {
            outerClassNames.put(cls.name, cls.outerClass);
        }
        if (cls.innerClasses != null) {
            for (Object obj : cls.innerClasses) {
                InnerClassNode node = (InnerClassNode) obj;
                if (node.outerName != null) {
                    outerClassNames.put(node.name, node.outerName);
                }
            }
        }
        if (cls.visibleAnnotations == null) {
            return;
        }
        for (Object o : cls.visibleAnnotations) {
            AnnotationNode a = (AnnotationNode) o;
            if (Type.getDescriptor(Reloadable.class).equals(a.desc)) {
                isReloadableMap.put(cls.name, true);
            } else if (Type.getDescriptor(NonReloadable.class).equals(a.desc)) {
                isReloadableMap.put(cls.name, false);
            }
        }
    }

    public boolean isReloadable(String className) {
        String internalName = className.replace('.', '/');
        return isReloadableInternal(internalName);
    }

    private boolean isReloadableInternal(String internalName) {
        {
            Boolean result = isReloadableMap.get(internalName);
            if (result != null) {
                return result;
            }
        }
        {
            String outer = outerClassNames.get(internalName);
            if (outer != null) {
                return isReloadableInternal(outer);
            }
        }

        String[] parts = internalName.split("/");
        return isReloadableByPackages(parts, parts.length - 1);
    }

    private boolean isReloadableByPackages(String[] parts, int length) {
        if (length == 0) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(parts[i]);
        }
        sb.append("/package-info");
        Boolean result = isReloadableMap.get(sb.toString());
        if (result != null) {
            return result;
        }
        return isReloadableByPackages(parts, length - 1);
    }
}
