package com.github.ruediste.rise.nonReloadable.front.reload;

import com.github.ruediste.rise.nonReloadable.front.reload.ReloadebleClassesIndex;
import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;

public class ReloadableClassesIndexTestHelper {

	public static void callOnChange(ReloadebleClassesIndex cache,
			ClassChangeTransaction trx) {
		cache.onChange(trx);
	}
}
