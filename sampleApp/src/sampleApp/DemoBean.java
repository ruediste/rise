package sampleApp;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.ManagedBean;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

//@LocalBean
//@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class DemoBean {

	@Inject
	UserTransaction ut;
	
	@PersistenceContext
	EntityManager em;
	
	
	public void readOnly(){
		try {
			ut.begin();
			read();
			ut.rollback();
		} catch (NotSupportedException| SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void read(){}
	private void readWrite(){}
	private void render(){}
	
	public void writeRenderCommit(){
		try {
			readWrite();
			ut.begin();
			Connection connection = em.unwrap(java.sql.Connection.class);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			render();
			ut.commit();
		} catch (NotSupportedException| SystemException | SecurityException | IllegalStateException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
