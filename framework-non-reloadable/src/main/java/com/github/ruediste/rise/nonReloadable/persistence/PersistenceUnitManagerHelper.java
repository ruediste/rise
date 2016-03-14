package com.github.ruediste.rise.nonReloadable.persistence;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import com.github.ruediste.rise.nonReloadable.front.CurrentRestartableApplicationHolder;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.AsmUtil;

@Singleton
public class PersistenceUnitManagerHelper {

    @Inject
    ClassHierarchyIndex index;

    @Inject
    CurrentRestartableApplicationHolder holder;

    public MutablePersistenceUnitInfo createUnit(Class<? extends Annotation> qualifier, String unitName) {
        ClassLoader classLoader = holder.getCurrentReloadableClassLoader();
        MutablePersistenceUnitInfo result = new MutablePersistenceUnitInfo();
        result.setExcludeUnlistedClasses(true);
        result.setValidationMode(ValidationMode.NONE);
        result.setPersistenceUnitName(unitName);
        result.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);

        try {
            Resource res = new PathMatchingResourcePatternResolver()
                    .getResource(DefaultPersistenceUnitManager.ORIGINAL_DEFAULT_PERSISTENCE_UNIT_ROOT_LOCATION);
            result.setPersistenceUnitRootUrl(res.getURL());
        } catch (IOException ex) {
            throw new PersistenceException("Unable to resolve persistence unit root URL", ex);
        }

        Set<String> jpaAnnotationNames = Arrays
                .stream(new Class<?>[] { Entity.class, MappedSuperclass.class, Embeddable.class })
                .map(x -> Type.getType(x).getInternalName()).collect(Collectors.toSet());

        classLoop: for (ClassNode classNode : index.getAllNodes()) {
            if (classNode.visibleAnnotations == null)
                continue;
            boolean qualifierFound = qualifier == null;
            boolean jpaAnnotationFound = false;
            for (AnnotationNode annotation : classNode.visibleAnnotations) {
                Type annotationType = Type.getType(annotation.desc);
                String annotationName = annotationType.getInternalName();
                if (qualifier == null) {
                    // check that this is not a qualifier annotation
                    Class<?> annotationClass = AsmUtil.loadClass(annotationType, classLoader);
                    if (annotationClass.isAnnotationPresent(Qualifier.class))
                        continue classLoop;
                } else {
                    // test if the annotation is the required qualifier
                    // annotation
                    if (Type.getType(qualifier).getInternalName().equals(annotationName))
                        qualifierFound = true;
                }

                // test if the annotation is one of the jpa annotations
                if (jpaAnnotationNames.contains(annotationName))
                    jpaAnnotationFound = true;
            }

            if (qualifierFound && jpaAnnotationFound)
                result.addManagedClassName(Type.getObjectType(classNode.name).getClassName());

        }
        return result;
    }
}
