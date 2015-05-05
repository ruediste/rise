package com.github.ruediste.laf.mvc.web;

import com.github.ruediste.laf.core.actionInvocation.ActionInvocation;
import com.github.ruediste.laf.core.persistence.TransactionControl;
import com.github.ruediste.laf.core.scopes.RequestScoped;

@RequestScoped
public class MvcWebRequestInfo {

	private ActionInvocation<String> stringActionInvocation;

	private boolean updating;

	private TransactionControl transactionControl;

	public MvcWebRequestInfo self() {
		return this;
	}

	public ActionInvocation<String> getStringActionInvocation() {
		return stringActionInvocation;
	}

	public void setStringActionInvocation(
			ActionInvocation<String> stringActionInvocation) {
		this.stringActionInvocation = stringActionInvocation;
	}

	public void setIsUpdating(boolean updating) {
		this.setUpdating(updating);
	}

	public boolean isUpdating() {
		return updating;
	}

	public void setUpdating(boolean updating) {
		this.updating = updating;
	}

	public void setTransactionControl(TransactionControl transactionControl) {
		this.transactionControl = transactionControl;

	}

	public TransactionControl getTransactionControl() {
		return transactionControl;
	}
}
