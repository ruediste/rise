package com.github.ruediste.rise.core.persistence;

import javax.persistence.Entity;

@Entity
public class TestEntityDerived extends TestEntity {

    private String derivedValue;

    public String getDerivedValue() {
        return derivedValue;
    }

    public void setDerivedValue(String derivedValue) {
        this.derivedValue = derivedValue;
    }
}
