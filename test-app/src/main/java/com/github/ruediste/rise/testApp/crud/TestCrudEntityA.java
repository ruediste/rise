package com.github.ruediste.rise.testApp.crud;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.github.ruediste.rise.crud.annotations.CrudBrowserColumn;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
public class TestCrudEntityA {

    @GeneratedValue
    @Id
    private long id;

    @CrudBrowserColumn
    private String stringValue;

    @ManyToOne
    private TestCrudEntityB entityB;

    public TestCrudEntityB getEntityB() {
        return entityB;
    }

    public void setEntityB(TestCrudEntityB entityB) {
        this.entityB = entityB;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
