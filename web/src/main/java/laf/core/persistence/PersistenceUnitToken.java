package laf.core.persistence;

/**
 * Token representing a persistence unit.
 */
public final class PersistenceUnitToken {
	private String persistenceUnitName;

	/**
	 * Package visible to avoid instantiation from client code
	 */
	PersistenceUnitToken(String persistenceUnitName) {
		super();
		this.persistenceUnitName = persistenceUnitName;
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	public void setPersistenceUnitName(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}
}
