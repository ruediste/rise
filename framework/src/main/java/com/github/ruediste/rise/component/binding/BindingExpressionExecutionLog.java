package com.github.ruediste.rise.component.binding;

import java.util.ArrayList;
import java.util.List;

import com.github.ruediste.rise.component.binding.BindingExpressionExecutionLogManager.MethodInvocation;

class BindingExpressionExecutionLog {
    BindingGroup<?> involvedBindingGroup;
    List<MethodInvocation> modelPath = new ArrayList<>();
    List<MethodInvocation> componentPath = new ArrayList<>();
    BindingTransformer<?, ?> transformer;
    boolean transformInv;
}