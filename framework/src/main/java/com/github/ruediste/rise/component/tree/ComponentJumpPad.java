package com.github.ruediste.rise.component.tree;

public class ComponentJumpPad {

    public static long getComponentNr(Component fragment) {
        return fragment.getFragmentNr();
    }

    public static void setFragmentNr(Component fragment, long nr) {
        fragment.setFragmentNr(nr);
    }
}
