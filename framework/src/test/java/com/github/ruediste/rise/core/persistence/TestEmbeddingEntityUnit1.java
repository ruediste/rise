package com.github.ruediste.rise.core.persistence;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.github.ruediste.rise.nonReloadable.persistence.NullUnit;

@Entity
@Unit1
@NullUnit
public class TestEmbeddingEntityUnit1 {

    @Id
    @GeneratedValue
    long id;

    @Embedded
    TestEmbeddable embeddable;
}
