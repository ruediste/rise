package com.github.ruediste.rise.core.front.reload;

import com.github.ruediste.rise.core.front.reload.ReloadebleClassesIndex;
import com.github.ruediste.rise.core.front.reload.ClassChangeNotifier.ClassChangeTransaction;

public class ClassSpaceCacheTestHelper {

	public static void callOnChange(ReloadebleClassesIndex cache,
			ClassChangeTransaction trx) {
		cache.onChange(trx);
	}
}
