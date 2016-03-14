package com.github.ruediste.rise.core.web;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.ruediste.rise.util.AsmUtil;

public class ActionPathAnnotationUtil {

    public static class MethodPathInfos {
        /**
         * Path infos, including the {@link #primaryPathInfo}
         */
        public List<String> pathInfos = new ArrayList<>();
        public String primaryPathInfo;
    }

    public static MethodPathInfos getPathInfos(MethodNode m, Supplier<String> defaultPathInfoSupplier) {
        MethodPathInfos result = new MethodPathInfos();
        String primaryPath = null;

        if (m.visibleAnnotations != null)
            for (AnnotationNode path : AsmUtil.getAnnotationsByType(m.visibleAnnotations, ActionPath.class)) {
                String pathValue = AsmUtil.getString(path, "value");
                result.pathInfos.add(pathValue);
                if (AsmUtil.tryGetBoolean(path, "primary").orElse(false)) {
                    if (primaryPath != null) {
                        throw new RuntimeException(
                                "Multiple ActionPath annotations with primary=true found on method " + m);
                    }
                    primaryPath = pathValue;
                }
            }
        if (!AsmUtil.getAnnotationsByType(m.visibleAnnotations, NoDefaultActionPath.class).isEmpty()) {
            if (primaryPath == null) {
                throw new RuntimeException(
                        "No ActionPath marked as primaryPath, but NoDefaultActionPath annotation present");
            }
        } else {
            String defaultPathInfo = defaultPathInfoSupplier.get();
            result.pathInfos.add(defaultPathInfo);

            // there is no primary path yet, use the default path
            if (primaryPath == null) {
                primaryPath = defaultPathInfo;
            }
        }

        result.primaryPathInfo = primaryPath;
        return result;
    }
}
