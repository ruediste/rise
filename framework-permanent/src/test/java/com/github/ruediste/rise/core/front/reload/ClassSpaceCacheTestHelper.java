package com.github.ruediste.rise.core.front.reload;

import com.github.ruediste.rise.core.front.reload.ClassSpaceCache;
import com.github.ruediste.rise.core.front.reload.ClassChangeNotifier.ClassChangeTransaction;

public class ClassSpaceCacheTestHelper {

	public static void callOnChange(ClassSpaceCache cache,
			ClassChangeTransaction trx) {
		cache.onChange(trx);
	}
}
