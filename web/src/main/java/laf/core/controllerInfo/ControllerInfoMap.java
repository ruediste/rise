package laf.core.controllerInfo;

import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * Bidirectional map from {@link ControllerInfo} to some data. The
 * initialization occurs when {@link #getMap()} is called for the first time.
 */
public class ControllerInfoMap<V> {
	@Inject
	ControllerInfoRepository controllerInfoRepository;

	private Function<ControllerInfo, V> valueLoader;

	private BiMap<ControllerInfo, V> map;

	public void initialize(Function<ControllerInfo, V> valueLoader) {
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
