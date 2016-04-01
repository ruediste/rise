package com.github.ruediste.rise.component.binding;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;

public class BindingExpressionExecutionRecord {
    private BindingGroup<?> involvedBindingGroup;
    MethodInvocationRecorder modelRecorder = new MethodInvocationRecorder();
    MethodInvocationRecorder componentRecorder = new MethodInvocationRecorder();
    BindingTransformer<?, ?> transformer;

    boolean transformInv;

    public BindingGroup<?> getInvolvedBindingGroup() {
        return involvedBindingGroup;
    }

    public void setInvolvedBindingGroup(BindingGroup<?> involvedBindingGroup) {
        if (this.involvedBindingGroup != null) {
            throw new RuntimeException(
                    "attemt to involve multiple binding groups in a single binding. Only one is allowed");
        }
        this.involvedBindingGroup = involvedBindingGroup;
    }
}