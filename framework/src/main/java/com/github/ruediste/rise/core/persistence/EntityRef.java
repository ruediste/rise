package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Annotation;

/**
 * A reference to an entity
 */
public class EntityRef<T> {
    Class<T> entityClass;
    Class<? extends Annotation> emQualifier;
    Object key;
}
