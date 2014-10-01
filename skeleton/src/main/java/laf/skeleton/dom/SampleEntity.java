package laf.skeleton.dom;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SampleEntity {

	@Id
	private long id;

	private int version;

	private String stringValue;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
}
