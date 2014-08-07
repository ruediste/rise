package laf.mvc;

import javax.enterprise.context.ApplicationScoped;

import laf.base.attachedProperties.AttachedProperty;
import laf.base.attachedProperties.AttachedPropertyBearer;
import laf.core.controllerInfo.ActionMethodInfo;

@ApplicationScoped
public class MvcService {

	private AttachedProperty<AttachedPropertyBearer, Boolean> isUpdating = new AttachedProperty<>();

	public boolean isUpdating(ActionMethodInfo info) {
		return isUpdating.isSet(info);
	}

	public void setUpdating(ActionMethodInfo info, boolean updating) {
		if (updating) {
			isUpdating.set(info, true);
		} else {
			isUpdating.clear(info);
		}
	}
}
