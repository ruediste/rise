package laf.component.transaction;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
public class TestEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String value;

	@ManyToOne
	private TestEntity parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private Set<TestEntity> children = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Set<TestEntity> getChildren() {
		return children;
	}

	public TestEntity getParent() {
		return parent;
	}

	public void setParent(TestEntity parent) {
		this.parent = parent;
	}

}
