package com.github.ruediste.laf.core.persistence;

import javax.transaction.TransactionManager;

import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.transaction.JTATransactionController;

import bitronix.tm.TransactionManagerServices;

/**
 * {@link ExternalTransactionController} for EclipseLink, forwarding to Bitronix
 */
public class BitronixTransactionController extends JTATransactionController {
	@Override
	protected TransactionManager acquireTransactionManager() throws Exception {
		return TransactionManagerServices.getTransactionManager();
	}
}
