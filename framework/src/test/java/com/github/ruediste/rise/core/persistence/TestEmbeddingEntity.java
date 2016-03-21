package com.github.ruediste.rise.core.persistence;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TestEmbeddingEntity {

    @Id
    @GeneratedValue
    int id;

    @Embedded
    TestEmbeddable embeddableA = new TestEmbeddable();

    @Embedded
    TestEmbeddable embeddableB = new TestEmbeddable();
}
