package laf.component.core.binding;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.google.common.reflect.TypeToken;

public class BindingGroupProducer {

	@SuppressWarnings("unchecked")
	@Produces
	public <T> BindingGroup<T> produce(InjectionPoint p) {
		Class<?> dataType = TypeToken.of(p.getType())
				.resolveType(BindingGroup.class.getTypeParameters()[0])
				.getRawType();
		return new BindingGroup<T>((Class<T>) dataType);
	}
}
