package com.github.ruediste.rise.testApp.crud;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
public class TestCrudEntityB {
    @GeneratedValue
    @Id
    long id;
}
