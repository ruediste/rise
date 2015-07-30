package com.github.ruediste.rise.sample.db;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.ruediste.rise.crud.annotations.CrudIdentifying;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
public class TodoCategory {

    @Id
    private long id;

    @CrudIdentifying
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
