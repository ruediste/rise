package com.github.ruediste.rise.nonReloadable.persistence;

import java.sql.Connection;

/**
 * Represents the different isolation levels defined by the JDBC standard
 */
public enum IsolationLevel {
	/**
	 * For updating transactions use {@link IsolationLevel#SERIALIZABLE}. For
	 * non-updating transactions, use {@link IsolationLevel#REPEATABLE_READ}
	 */
	DEFAULT(-1) {
		@Override
		public boolean isLowerThan(IsolationLevel other) {
			throw new UnsupportedOperationException();
		}
	},

	/**
	 * A constant indicating that dirty reads, non-repeatable reads and phantom
	 * reads can occur. This level allows a row changed by one transaction to be
	 * read by another transaction before any changes in that row have been
	 * committed (a "dirty read"). If any of the changes are rolled back, the
	 * second transaction will have retrieved an invalid row.
	 */
	READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED) {
		@Override
		public boolean isLowerThan(IsolationLevel other) {
			if (other == IsolationLevel.DEFAULT)
				throw new UnsupportedOperationException();
			if (other == this)
				return false;
			return true;
		}
	},

	/**
	 * A constant indicating that dirty reads are prevented; non-repeatable
	 * reads and phantom reads can occur. This level only prohibits a
	 * transaction from reading a row with uncommitted changes in it.
	 */
	READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED) {
		@Override
		public boolean isLowerThan(IsolationLevel other) {
			if (other == IsolationLevel.DEFAULT)
				throw new UnsupportedOperationException();
			if (other == this || other == READ_UNCOMMITTED)
				return false;
			return true;
		}
	},

	/**
	 * A constant indicating that dirty reads and non-repeatable reads are
	 * prevented; phantom reads can occur. This level prohibits a transaction
	 * from reading a row with uncommitted changes in it, and it also prohibits
	 * the situation where one transaction reads a row, a second transaction
	 * alters the row, and the first transaction rereads the row, getting
	 * different values the second time (a "non-repeatable read").
	 */
	REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ) {
		@Override
		public boolean isLowerThan(IsolationLevel other) {
			if (other == IsolationLevel.DEFAULT)
				throw new UnsupportedOperationException();
			if (other == IsolationLevel.SERIALIZABLE)
				return true;
			return false;
		}
	},

	/**
	 * A constant indicating that dirty reads, non-repeatable reads and phantom
	 * reads are prevented. This level includes the prohibitions in
	 * <code>TRANSACTION_REPEATABLE_READ</code> and further prohibits the
	 * situation where one transaction reads all rows that satisfy a
	 * <code>WHERE</code> condition, a second transaction inserts a row that
	 * satisfies that <code>WHERE</code> condition, and the first transaction
	 * rereads for the same condition, retrieving the additional "phantom" row
	 * in the second read.
	 */
	SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE) {
		@Override
		public boolean isLowerThan(IsolationLevel other) {
			if (other == IsolationLevel.DEFAULT)
				throw new UnsupportedOperationException();
			return false;
		}
	};

	final private int level;

	private IsolationLevel(int level) {
		this.level = level;
	}

	/**
	 * Return the level as defined by the
	 * {@link Connection#TRANSACTION_READ_UNCOMMITTED
	 * Connection.TRANSACTION_XXX} constants
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Return true if this isolation is lower than the other
	 */
	public abstract boolean isLowerThan(IsolationLevel other);
}
