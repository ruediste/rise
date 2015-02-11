package com.github.ruediste.laf.core.persistence;

import static org.junit.Assert.*;

import java.sql.*;

import javax.persistence.metamodel.EntityType;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;

import bitronix.tm.resource.jdbc.PoolingDataSource;

import com.github.ruediste.laf.test.TestEntity;

public class PersistenceUnitTokenManagerTest {

	private Connection connection;
	private PersistenceUnitTokenManager mgr;

	@Before
	public void setup() throws SQLException {

		mgr = new PersistenceUnitTokenManager();
	}

	protected void openDBConnection() throws SQLException {
		connection = DriverManager
				.getConnection("jdbc:h2:mem:testdb", "sa", "");

		PoolingDataSource myDataSource = new PoolingDataSource();
		myDataSource.setClassName(JdbcDataSource.class.getName());
		myDataSource.setUniqueName("h2");
		myDataSource.setMaxPoolSize(5);
		myDataSource.setAllowLocalTransactions(false);
		myDataSource.setTestQuery("SELECT 1");
		myDataSource.getDriverProperties().setProperty("user", "sa");
		myDataSource.getDriverProperties().setProperty("password", "");
		myDataSource.getDriverProperties().setProperty("URL",
				"jdbc:h2:mem:testdb");
		myDataSource.init();
	}

	@After
	public void after() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
		}
	}

	@Test
	public void testCreateTokenString() throws Exception {
		PersistenceUnitToken token = mgr.createToken("unitTest").build();
		assertEquals("unitTest", token.getPersistenceUnitName());
	}

	@Test
	public void testGetToken() throws Exception {
		PersistenceUnitToken token = mgr.createToken("unitTest").build();
		assertSame(token, mgr.getToken("unitTest"));
	}

	@Test
	public void testGetEntityMetaModel() throws Exception {
		PersistenceUnitToken token = mgr.createToken("unitTest").build();

		openDBConnection();

		EntityType<?> type = mgr.getEntityMetaModel(token, TestEntity.class);
		assertEquals("TestEntity", type.getName());
	}

}
