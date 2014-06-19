package sampleApp.entities;

import javax.persistence.*;

@MappedSuperclass
public class SimpleIdEntity {

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	public Long getId() {
		return id;
	}

	public Long getVersion() {
		return version;
	}
}
