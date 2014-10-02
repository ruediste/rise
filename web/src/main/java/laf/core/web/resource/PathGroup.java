package laf.core.web.resource;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathGroup {
	private final StaticWebResourceBundle bundle;
	private final List<String> paths;

	public PathGroup(StaticWebResourceBundle bundle, List<String> paths) {
		this.bundle = bundle;
		this.paths = paths;
	}

	public PathGroup(StaticWebResourceBundle bundle, Stream<String> paths) {
		this(bundle, paths.collect(Collectors.toList()));
	}

	public PathGroup merge(PathGroup other) {
		List<String> l = new ArrayList<>();
		l.addAll(getPaths());
		l.addAll(other.getPaths());
		return new PathGroup(bundle, l);
	}

	public PathGroup insertMinInProd() {
		if (bundle.getMode() == ResourceMode.PRODUCTION) {
			return map(this::insertMin);
		} else {
			return this;
		}
	}

	public ResourceGroup load(Function<PathGroup, ResourceGroup> loader) {
		return loader.apply(this);
	}

	public PathGroup map(Function<String, String> mapper) {
		return new PathGroup(bundle, getPaths().stream().map(mapper));
	}

	public PathGroup filter(Predicate<String> filter) {
		return new PathGroup(bundle, getPaths().stream().filter(filter));
	}

	String insertMin(String path) {
		String[] parts = path.split("\\.");
		if (parts.length == 1) {
			return path;
		} else {
			return Arrays.asList(parts).subList(0, parts.length - 1).stream()
					.collect(Collectors.joining("."))
					+ ".min." + parts[parts.length - 1];
		}
	}

	public List<String> getPaths() {
		return Collections.unmodifiableList(paths);
	}

}
