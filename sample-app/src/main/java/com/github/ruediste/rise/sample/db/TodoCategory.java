package com.github.ruediste.rise.sample.db;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.github.ruediste.rise.crud.annotations.CrudIdentifying;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
public class TodoCategory {

    @GeneratedValue
    @Id
    private long id;

    @CrudIdentifying
    private String name;

    // @OneToMany
    @Transient
    private Set<TodoItem> todoItems;

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

    public Set<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(Set<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }
}
