package laf.mvc;

import javax.enterprise.context.ApplicationScoped;

import laf.attachedProperties.AttachedProperty;
import laf.attachedProperties.AttachedPropertyBearer;
import laf.controllerInfo.ActionMethodInfo;

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
