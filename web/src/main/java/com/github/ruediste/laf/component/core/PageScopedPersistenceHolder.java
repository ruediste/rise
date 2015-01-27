package com.github.ruediste.laf.component.core;

import java.io.Serializable;

import javax.enterprise.inject.Typed;

import com.github.ruediste.laf.component.core.pageScope.PageScoped;
import com.github.ruediste.laf.core.persistence.LafPersistenceHolder;

@PageScoped
@Typed(PageScopedPersistenceHolder.class)
public class PageScopedPersistenceHolder extends LafPersistenceHolder implements
		Serializable {
	private static final long serialVersionUID = 1L;

}
