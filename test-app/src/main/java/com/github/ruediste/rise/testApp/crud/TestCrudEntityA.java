package com.github.ruediste.rise.testApp.crud;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.Length;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.InjectParameter;
import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.core.persistence.em.EntityManagerHolder;
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
	private void setStringValueAction(@Labeled String newValue, @InjectParameter TransactionControl trx,
			@InjectParameter EntityManagerHolder emh) {
		trx.updating().execute(() -> {
			emh.joinTransaction();
			stringValue = newValue;
		});
	}

	@CrudDisplayAction
	@Labeled
	@GlyphiconIcon(Glyphicon.bullhorn)
	private void setStringValueToFoo(@InjectParameter TransactionControl trx,
			@InjectParameter EntityManagerHolder emh) {
		trx.updating().execute(() -> {
			emh.joinTransaction();
			stringValue = "foo";
		});
	}

}
