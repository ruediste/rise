package com.github.ruediste.laf.core.front.reload;

import com.github.ruediste.laf.core.front.reload.ClassChangeNotifier.ClassChangeTransaction;

public class ClassSpaceCacheTestHelper {

	public static void callOnChange(ClassSpaceCache cache,
			ClassChangeTransaction trx) {
		cache.onChange(trx);
	}
}
