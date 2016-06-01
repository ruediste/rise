package com.github.ruediste.rise.component.components;

import com.github.ruediste.rise.component.tree.ComponentBase;

@DefaultTemplate(CRunnableTemplate.class)
public class CRunnable extends ComponentBase<CRunnable> {

    private final Runnable runnable;

    public CRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
