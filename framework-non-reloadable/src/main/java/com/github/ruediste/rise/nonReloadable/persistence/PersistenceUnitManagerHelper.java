package com.github.ruediste.rise.nonReloadable.persistence;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import com.github.ruediste.rise.nonReloadable.CoreConfigurationNonRestartable;
import com.github.ruediste.rise.nonReloadable.front.CurrentRestartableApplicationHolder;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassHierarchyIndex;
import com.github.ruediste.rise.util.AsmUtil;

@Singleton
public class PersistenceUnitManagerHelper {

    @Inject
    ClassHierarchyIndex index;

    @Inject
    CurrentRestartableApplicationHolder holder;

    @Inject
    CoreConfigurationNonRestartable config;

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

        result.addProperty(PersistenceUnitProperties.SESSION_CUSTOMIZER, CompositeSessionCustomizer.class.getName());

        Set<Class<?>> jpaAnnotations = new HashSet<>(
                Arrays.asList(Entity.class, MappedSuperclass.class, Embeddable.class));
        for (ClassNode classNode : index.getAllNodes()) {
            String className = Type.getObjectType(classNode.name).getClassName();

            if (classNode.visibleAnnotations == null)
                continue;
            boolean anyQualifierFound = false;
            boolean requiredPUAnnotationFound = false;
            boolean jpaAnnotationFound = false;
            for (AnnotationNode annotation : classNode.visibleAnnotations) {
                Type annotationType = Type.getType(annotation.desc);
                Class<?> annotationClass = AsmUtil.loadClass(annotationType, classLoader);
                anyQualifierFound |= annotationClass.isAnnotationPresent(Qualifier.class);
                requiredPUAnnotationFound |= AnyUnit.class.equals(annotationClass);
                if (qualifier == null) {
                    requiredPUAnnotationFound |= NullUnit.class.equals(annotationClass);
                } else {
                    requiredPUAnnotationFound |= qualifier.equals(annotationClass);
                }

                // test if the annotation is one of the jpa annotations
                if (jpaAnnotations.contains(annotationClass))
                    jpaAnnotationFound = true;
            }

            if (jpaAnnotationFound && (requiredPUAnnotationFound || (qualifier == null && !anyQualifierFound))) {
                result.addManagedClassName(className);
            }

        }
        return result;
    }
}
