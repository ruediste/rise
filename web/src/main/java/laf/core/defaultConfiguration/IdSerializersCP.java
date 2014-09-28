package laf.core.defaultConfiguration;

import java.util.Deque;

import laf.core.argumentSerializer.defaultSerializers.IdentifierSerializer;
import laf.core.base.configuration.ConfigurationParameter;

public interface IdSerializersCP extends
		ConfigurationParameter<Deque<IdentifierSerializer>> {

}
