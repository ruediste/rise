package laf.core.web.resource.v2;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.hash.Hashing;

public class ResourceBase<T extends ResourceBase<T>> {

	final public String name;
	final public byte[] data;

	public ResourceBase(DataSource dataSource) {
		name = dataSource.name;
		data = dataSource.data;
	}

	public ResourceBase(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	protected T self() {
		return (T) this;
	}

	protected String resolveNameTemplate(String template) {
		Pattern p = Pattern
				.compile("(\\A|[^\\\\])\\{(?<placeholder>[^\\}]*)\\}");
		Matcher m = p.matcher(template);
		StringBuilder sb = new StringBuilder();
		int lastEnd = 0;
		while (m.find()) {
			sb.append(template.substring(lastEnd,
					m.start() == 0 ? 0 : m.start() + 1));
			lastEnd = m.end();
			String placeholder = m.group("placeholder");
			switch (placeholder) {
			case "hash":
				sb.append(Hashing.sha256().hashBytes(data).toString());
				break;
			case "name": {
				String[] parts = name.split("/");
				parts = parts[parts.length - 1].split("\\.");

				sb.append(Arrays.asList(parts)
						.subList(0, parts.length == 1 ? 1 : parts.length - 1)
						.stream().collect(Collectors.joining(".")));
			}
				break;
			default:
				throw new RuntimeException("Unknown placeholder " + placeholder);
			}
		}
		sb.append(template.substring(lastEnd, template.length()));
		return sb.toString();
	}
}
