package com.github.ruediste.rise.sample.db;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
@Labeled
public class TodoCategory {

    @GeneratedValue
    @Id
    private long id;

    private String name;

    @OneToMany(mappedBy = "category")
    private Set<TodoItem> todoItems = new HashSet<>();

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
