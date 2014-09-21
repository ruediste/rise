package laf.core.web.resource.v2;

import java.util.List;
import java.util.function.Function;

public class DataSourceGroup {

	List<DataSource> sources;

	public <T> ResourceGroup<T> toResourceGroup(ResourceContext ctx,
			Function<DataSource, T> creator) {
		return new ResourceGroup<>(ctx, this, creator);
	}
}
