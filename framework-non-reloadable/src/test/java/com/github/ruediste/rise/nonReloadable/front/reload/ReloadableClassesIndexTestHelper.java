package com.github.ruediste.rise.nonReloadable.front.reload;

import com.github.ruediste.rise.nonReloadable.front.reload.ClassChangeNotifier.ClassChangeTransaction;

public class ReloadableClassesIndexTestHelper {

    public static void callOnChange(ReloadableClassesIndex cache, ClassChangeTransaction trx) {
        cache.onChange(trx);
    }
}
