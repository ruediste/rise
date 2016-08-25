package com.github.ruediste.rise.component.tree;

public class FragmentJumpPad {

    public static long getFragmentNr(Component fragment) {
        return fragment.getFragmentNr();
    }

    public static void setFragmentNr(Component fragment, long nr) {
        fragment.setFragmentNr(nr);
    }
}
