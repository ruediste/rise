package laf.core.web.resource.v2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import laf.core.base.Function2;

/**
 * Represents a group of resources
 */
public class ResourceGroup<T extends ResourceBase<T>> {
	public List<T> resources;
	public ResourceContext ctx;

	public ResourceGroup(ResourceContext ctx, List<T> resources) {
		this.ctx = ctx;
		this.resources = resources;
	}

	public ResourceGroup(ResourceContext ctx, DataSourceGroup group,
			Function<DataSource, T> creator) {
		this(ctx, group.sources.stream().map(creator)
				.collect(Collectors.toList()));

	}

	public <R extends ResourceBase<R>> ResourceGroup<R> process(
			Function<? super T, ? extends R> processor) {
		return new ResourceGroup<>(ctx, resources.stream().map(processor)
				.collect(Collectors.toList()));
	}

	public void send(Consumer<? super T> consumer) {
		resources.forEach(consumer);
	}

	@SafeVarargs
	final public ResourceGroup<T> fork(
			final Consumer<ResourceGroup<T>>... consumers) {
		for (Consumer<ResourceGroup<T>> consumer : consumers) {
			consumer.accept(this);
		}

		return this;
	}

	public ResourceGroup<T> filter(ResourceMode mode) {
		if (ctx.mode == mode) {
			return this;
		} else {
			return new ResourceGroup<>(ctx, Collections.emptyList());
		}

	}

	public ResourceGroup<T> prod() {
		return filter(ResourceMode.PRODUCTION);
	}

	public ResourceGroup<T> dev() {
		return filter(ResourceMode.DEVELOPMENT);
	}

	public ResourceGroup<T> merge(ResourceGroup<? extends T> other) {
		ArrayList<T> list = new ArrayList<>();
		list.addAll(resources);
		list.addAll(other.resources);
		return new ResourceGroup<>(ctx, list);
	}

	public ResourceGroup<T> collect(String name,
			Function2<String, byte[], T> creator) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			for (T res : resources) {
				baos.write(res.data);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ResourceGroup<T>(ctx, Collections.singletonList(creator
				.apply(name, baos.toByteArray())));

	}

	public ResourceGroup<T> name(String template,
			Function2<String, byte[], T> creator) {
		return new ResourceGroup<>(ctx,
				resources
						.stream()
						.map(x -> creator.apply(
								x.resolveNameTemplate(template), x.data))
						.collect(Collectors.toList()));
	}
}
