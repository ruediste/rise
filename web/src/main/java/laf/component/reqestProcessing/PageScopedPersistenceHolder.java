package laf.component.reqestProcessing;

import java.io.Serializable;

import laf.component.pageScope.PageScoped;
import laf.persistence.LafPersistenceHolderBase;

@PageScoped
public class PageScopedPersistenceHolder extends LafPersistenceHolderBase
		implements Serializable {
	private static final long serialVersionUID = 1L;

}
