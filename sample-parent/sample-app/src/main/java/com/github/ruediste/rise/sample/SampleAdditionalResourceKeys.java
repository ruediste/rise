package com.github.ruediste.rise.sample;

import com.github.ruediste1.i18n.lString.AdditionalResourceKeyProvider;

/**
 * Add an additional resource key. This class is automatically picked up by
 * RISE.
 */
public class SampleAdditionalResourceKeys implements AdditionalResourceKeyProvider {

    @Override
    public void provideKeys(KeyReceiver receiver) {
        receiver.add("sample.additional.key", "test1");
    }

}
