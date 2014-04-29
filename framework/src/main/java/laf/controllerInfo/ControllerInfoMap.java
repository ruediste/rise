package laf.controllerInfo;

import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * Lazily initialized bidirectional map from {@link ControllerInfo} to some
 * data.
 */
public class ControllerInfoMap<V> {

	private final ControllerInfoRepository controllerInfoRepository;

	private final Function<ControllerInfo, V> valueLoader;

	public static class Builder {
		@Inject
		ControllerInfoRepository controllerInfoRepository;

		public <V> ControllerInfoMap<V> create(
				Function<ControllerInfo, V> valueLoader) {
			return new ControllerInfoMap<>(controllerInfoRepository,
					valueLoader);
		}
	}

	private BiMap<ControllerInfo, V> map;

	ControllerInfoMap(ControllerInfoRepository controllerInfoRepository,
			Function<ControllerInfo, V> valueLoader) {
		this.controllerInfoRepository = controllerInfoRepository;
		this.valueLoader = valueLoader;
	}

	/**
	 * Get the {@link BiMap}. Lazily initializes the map the fist time it is
	 * accessed. Thread safe.
	 *
	 * @throws IllegalArgumentException
	 *             if duplicate values were generated
	 */
	public BiMap<ControllerInfo, V> getMap() {
		if (map != null) {
			return map;
		}
		synchronized (this) {
			if (map == null) {
				com.google.common.collect.ImmutableBiMap.Builder<ControllerInfo, V> builder = ImmutableBiMap
						.builder();
				// fill the map
				for (ControllerInfo info : controllerInfoRepository
						.getControllerInfos()) {
					builder.put(info, valueLoader.apply(info));
				}
				map = builder.build();
			}
			return map;
		}
	}
}
