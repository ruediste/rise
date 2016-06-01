package com.github.ruediste.rise.component.fragment;

import javax.inject.Inject;

public abstract class FragmentCanvasBase<TSelf extends FragmentCanvasBase<TSelf>> implements FragmentCanvas<TSelf> {

    @Inject
    public FragmentCanvasTarget target;

    @Override
    public FragmentCanvasTarget internal_target() {
        return target;
    }

    public FragmentCanvasTarget getTarget() {
        return target;
    }
}
