package com.github.ruediste.rise.core.persistence;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * A reference to an entity
 */
public class EntityRef<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    Class<T> entityClass;
    Class<? extends Annotation> emQualifier;
    Object key;
}
