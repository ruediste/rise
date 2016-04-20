package com.github.ruediste.rise.testApp.crud;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.Length;

import com.github.ruediste.c3java.properties.NoPropertyAccessor;
import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.component.generic.ActionMethodInvocationResult;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.crud.CrudControllerBase;
import com.github.ruediste.rise.crud.annotations.CrudBrowserColumn;
import com.github.ruediste.rise.crud.annotations.CrudDisplayAction;
import com.github.ruediste.rise.integration.GlyphiconIcon;
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

    @CrudBrowserColumn
    @ManyToOne
    private TestCrudEntityB entityB;

    @Length(min = 3, max = 10)
    private String constrainedValue;

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

    public String getConstrainedValue() {
        return constrainedValue;
    }

    public void setConstrainedValue(String constrainedValue) {
        this.constrainedValue = constrainedValue;
    }

    @CrudDisplayAction
    @Labeled
    @GlyphiconIcon(Glyphicon.bullhorn)
    @Updating
    @NoPropertyAccessor
    public void setStringValueAction(@Labeled String newValue) {
        stringValue = newValue;
    }

    @CrudDisplayAction
    @Labeled
    @GlyphiconIcon(Glyphicon.bullhorn)
    @Updating
    @NoPropertyAccessor
    public void setStringValueToFoo() {
        stringValue = "foo";
    }

    @PropertiesLabeled
    public static class Result {
        private String calculatedValue;

        public String getCalculatedValue() {
            return calculatedValue;
        }

        public void setCalculatedValue(String calculatedValue) {
            this.calculatedValue = calculatedValue;
        }
    }

    @CrudDisplayAction
    @Labeled
    @GlyphiconIcon(Glyphicon.bullhorn)
    public Result calculateWithValue() {
        Result result = new Result();
        result.calculatedValue = stringValue + "123";
        return result;
    }

    @CrudDisplayAction
    @Labeled
    @GlyphiconIcon(Glyphicon.bullhorn)
    @Updating
    public ActionMethodInvocationResult redirectToBrowse() {
        return ActionMethodInvocationResult.redirect(CrudControllerBase.class,
                x -> x.browse(TestCrudEntityA.class, null));
    }

}
