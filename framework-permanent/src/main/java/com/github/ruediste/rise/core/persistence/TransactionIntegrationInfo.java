package com.github.ruediste.rise.core.persistence;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.sessions.ExternalTransactionController;

/**
 * Provide information for providers of {@link EntityManagerFactory}ies on how
 * to integrate with the transaction management
 */
public interface TransactionIntegrationInfo {

	Class<? extends ExternalTransactionController> getEclipseLinkExternalTransactionController();
}
