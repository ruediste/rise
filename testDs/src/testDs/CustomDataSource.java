package testDs;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.mysql.jdbc.ConnectionImpl;
import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlXADataSource;

public class CustomDataSource extends MysqlXADataSource {

	private static ThreadLocal<Integer> isolation=new ThreadLocal<Integer>(){
		protected Integer initialValue() {
			return Connection.TRANSACTION_REPEATABLE_READ;
		};
	};
	
	public static void setTransactionIsolation(int i){
		isolation.set(i);
	}
	
	private static final long serialVersionUID = 1L;

	private static class XAConnectionWrapper implements XAConnection {

		private XAConnection delegate;

		public XAConnectionWrapper(XAConnection delegate) {
			this.delegate = delegate;
		}

		@Override
		public Connection getConnection() throws SQLException {
			return delegate.getConnection();
		}

		@Override
		public void close() throws SQLException {
			delegate.close();
		}

		@Override
		public void addConnectionEventListener(ConnectionEventListener listener) {
			delegate.addConnectionEventListener(listener);
		}

		@Override
		public void removeConnectionEventListener(
				ConnectionEventListener listener) {
			delegate.removeConnectionEventListener(listener);
		}

		@Override
		public void addStatementEventListener(StatementEventListener listener) {
			delegate.addStatementEventListener(listener);
		}

		@Override
		public void removeStatementEventListener(StatementEventListener listener) {
			delegate.removeStatementEventListener(listener);
		}

		@Override
		public XAResource getXAResource() throws SQLException {
			return new XAResourceWrapper(delegate.getXAResource());
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public int hashCode() {
			return delegate.hashCode();
		}
	}

	private static class XAResourceWrapper implements XAResource {
		private XAResource delegate;
		private Field field;

		public void commit(Xid xid, boolean onePhase) throws XAException {
			delegate.commit(xid, onePhase);
		}

		public void end(Xid xid, int flags) throws XAException {
			delegate.end(xid, flags);
		}

		public void forget(Xid xid) throws XAException {
			delegate.forget(xid);
		}

		public int getTransactionTimeout() throws XAException {
			return delegate.getTransactionTimeout();
		}

		public boolean isSameRM(XAResource xares) throws XAException {
			return delegate.isSameRM(xares);
		}

		public int prepare(Xid xid) throws XAException {
			return delegate.prepare(xid);
		}

		public Xid[] recover(int flag) throws XAException {
			return delegate.recover(flag);
		}

		public void rollback(Xid xid) throws XAException {
			delegate.rollback(xid);
		}

		public boolean setTransactionTimeout(int seconds) throws XAException {
			return delegate.setTransactionTimeout(seconds);
		}

		public void start(Xid xid, int flags) throws XAException {
			try {
				ConnectionImpl connection = (ConnectionImpl) field
						.get(delegate);
				connection
						.setTransactionIsolation(isolation.get());
			} catch (IllegalArgumentException | IllegalAccessException
					| SQLException e) {
				throw new RuntimeException(e);
			}
			delegate.start(xid, flags);
		}

		public XAResourceWrapper(XAResource delegate) {
			try {
				field = MysqlXAConnection.class
						.getDeclaredField("underlyingConnection");
				field.setAccessible(true);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			this.delegate = delegate;
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public int hashCode() {
			return delegate.hashCode();
		}
	}

	@Override
	public XAConnection getXAConnection() throws SQLException {
		XAConnection conn = super.getXAConnection();

		return new XAConnectionWrapper(conn);
	}

	@Override
	public XAConnection getXAConnection(String u, String p) throws SQLException {
		return new XAConnectionWrapper(super.getXAConnection(u, p));
	}
}
