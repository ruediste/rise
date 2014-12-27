package sampleApp.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

import laf.core.translation.label.*;

@Labeled
@PropertiesLabeled(variants = { "short" })
@Entity
public class User extends SimpleIdEntity {

	public static final String foo = "foo";

	@Pattern.List({ @Pattern(regexp = "ABC", message = "Must match ABC"),
			@Pattern(regexp = "AB.*") })
	private String fistName;

	@Label(value = "LN", variant = "short")
	private String lastName;
	private Date lastLogin;

	public String getFistName() {
		return fistName;
	}

	public void setFistName(String fistName) {
		this.fistName = fistName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
}
