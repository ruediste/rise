package com.github.ruediste.rise.api;

import com.github.ruediste.rise.component.binding.Binding;

public class ApiJumpPad {

    public static void registerBinding(SubControllerComponent ctrl, Binding binding) {
        ctrl.registerBinding(binding);
    }
}
