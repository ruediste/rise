package laf.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

/**
 * Repository containing all defined {@link ConfigurationParameter}s
 */
@Singleton
public class ConfigurationParameterRepository {
	public static class ParameterEntry {
		public final ConfigurationParameter<?> parameter;
		public final Field field;
		public final Object instance;

		public ParameterEntry(ConfigurationParameter<?> parameter, Field field,
				Object instance) {
			this.parameter = parameter;
			this.field = field;
			this.instance = instance;
		}

	}

	private final ArrayList<ParameterEntry> entries = new ArrayList<>();

	public void addEntry(ParameterEntry entry) {
		entries.add(entry);
	}

	public List<ParameterEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public void addEntries(Collection<ParameterEntry> newEntries) {
		this.entries.addAll(newEntries);
	}
}
