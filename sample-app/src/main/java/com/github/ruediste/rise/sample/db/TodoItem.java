package com.github.ruediste.rise.sample.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
public class TodoItem {

    @GeneratedValue
    @Id
    private long id;

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
