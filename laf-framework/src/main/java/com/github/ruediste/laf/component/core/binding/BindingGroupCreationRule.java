package com.github.ruediste.laf.component.core.binding;

import com.github.ruediste.salta.core.CoreDependencyKey;
import com.github.ruediste.salta.core.CreationRuleImpl;

public class BindingGroupCreationRule extends CreationRuleImpl {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BindingGroupCreationRule() {
		super(CoreDependencyKey.rawTypeMatcher(BindingGroup.class),
				key -> () -> {
					Class<?> dataType = key
							.getType()
							.resolveType(
									BindingGroup.class.getTypeParameters()[0])
							.getRawType();
					return new BindingGroup(dataType);
				});
	}

}
