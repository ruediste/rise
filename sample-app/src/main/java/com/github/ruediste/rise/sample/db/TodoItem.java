package com.github.ruediste.rise.sample.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.github.ruediste.rise.crud.annotations.CrudBrowserColumn;
import com.github.ruediste1.i18n.label.Label;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
@Labeled
@Label(value = "Todo Items", variant = "plural")
public class TodoItem {

    @GeneratedValue
    @Id
    private long id;

    @CrudBrowserColumn
    private String name;

    @ManyToOne
    private TodoCategory category;

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

    public TodoCategory getCategory() {
        return category;
    }

    public void setCategory(TodoCategory category) {
        this.category = category;
    }

}
