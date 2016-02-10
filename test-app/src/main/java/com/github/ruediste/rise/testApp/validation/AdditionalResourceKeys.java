package com.github.ruediste.rise.testApp.validation;

import org.hibernate.validator.constraints.Length;

import com.github.ruediste1.i18n.lString.AdditionalResourceKeyProvider;

public class AdditionalResourceKeys implements AdditionalResourceKeyProvider {

    @Override
    public void provideKeys(KeyReceiver receiver) {
        receiver.addMessage(Length.class,
                "len must be between {min} and {max}");
    }

}
