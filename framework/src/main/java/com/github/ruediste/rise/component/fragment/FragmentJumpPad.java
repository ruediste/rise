package com.github.ruediste.rise.component.fragment;

public class FragmentJumpPad {

    public static long getFragmentNr(HtmlFragment fragment) {
        return fragment.getFragmentNr();
    }

    public static void setFragmentNr(HtmlFragment fragment, long nr) {
        fragment.setFragmentNr(nr);
    }
}
