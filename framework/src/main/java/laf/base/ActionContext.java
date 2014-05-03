package laf.base;

import javax.enterprise.context.RequestScoped;

import laf.attachedProperties.AttachedPropertyBearer;

@RequestScoped
public interface ActionContext extends AttachedPropertyBearer {

}
