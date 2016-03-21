package com.github.ruediste.rise.core.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.github.ruediste.rise.nonReloadable.persistence.AnyUnit;

@Entity
@AnyUnit
public class TestEntityAnyUnit {

    @Id
    @GeneratedValue
    long id;
}
