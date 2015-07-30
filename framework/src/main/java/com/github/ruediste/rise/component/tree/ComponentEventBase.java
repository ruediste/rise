package com.github.ruediste.rise.component.tree;

public class ComponentEventBase implements ComponentEvent {

    private ComponentEventType type;
    boolean canceled;

    protected ComponentEventBase(ComponentEventType type) {
        this.type = type;
    }

    @Override
    public ComponentEventType getType() {
        return type;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

}
