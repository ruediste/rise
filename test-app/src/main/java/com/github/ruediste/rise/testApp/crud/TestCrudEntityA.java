package com.github.ruediste.rise.testApp.crud;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.Length;

import com.github.ruediste.rise.crud.annotations.CrudBrowserColumn;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
@Labeled
public class TestCrudEntityA {

    @GeneratedValue
    @Id
    private long id;

    @CrudBrowserColumn
    private String stringValue;

    @ManyToOne
    private TestCrudEntityB entityB;

    @Length(min = 3, max = 10)
    String constrainedValue;

    private byte[] byteArray;

    @Embedded
    private TestCrudEmbeddable embeddable = new TestCrudEmbeddable();

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

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public TestCrudEmbeddable getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(TestCrudEmbeddable embeddable) {
        this.embeddable = embeddable;
    }

}
