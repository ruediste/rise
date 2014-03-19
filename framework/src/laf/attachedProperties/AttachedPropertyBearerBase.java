package laf.attachedProperties;

public class AttachedPropertyBearerBase implements AttachedPropertyBearer {

	private final AttachedPropertyMap map = new AttachedPropertyMap();

	@Override
	public AttachedPropertyMap getAttachedPropertyMap() {
		return map;
	}

}
