package com.github.ruediste.rise.testApp.crud;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
@Labeled
public class TestCrudEntityB {
    @GeneratedValue
    @Id
    private long id;

    @OneToMany(mappedBy = "entityB")
    private List<TestCrudEntityA> entityAs = new ArrayList<TestCrudEntityA>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<TestCrudEntityA> getEntityAs() {
        return entityAs;
    }

    public void setEntityAs(List<TestCrudEntityA> entityAs) {
        this.entityAs = entityAs;
    }

}
