package laf.base;

import javax.enterprise.context.RequestScoped;

import laf.base.attachedProperties.AttachedPropertyBearer;

/**
 * Bearer for request scoped information
 */
@RequestScoped
public interface ActionContext extends AttachedPropertyBearer {

}
