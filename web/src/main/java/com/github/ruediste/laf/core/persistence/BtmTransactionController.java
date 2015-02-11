package com.github.ruediste.laf.core.persistence;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.eclipse.persistence.transaction.JTATransactionController;

public class BtmTransactionController extends JTATransactionController {

	@Override
	protected TransactionManager acquireTransactionManager() throws Exception {
		return (TransactionManager) new InitialContext().lookup("java:comp/UserTransaction");
	}

}
