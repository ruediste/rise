package com.github.ruediste.rise.component.components;

public enum InputType {

    hidden, text, search, tel, url, email, password, datetime, date, //
    month, week, time, datetime_local, number, range, color, checkbox, radio, //
    file, submit, image, reset, button;

    @Override
    public String toString() {
        return name().replace("_", "-");
    }
}
