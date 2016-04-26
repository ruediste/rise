package com.github.ruediste.rise.component.fragment;

public abstract class FragmentCanvasBase<TSelf extends FragmentCanvasBase<TSelf>> implements FragmentCanvas<TSelf> {

    FragmentCanvasTarget target = new FragmentCanvasTarget();

    @Override
    public FragmentCanvasTarget internal_target() {
        return target;
    }

    public FragmentCanvasTarget getTarget() {
        return target;
    }
}
