package laf.component.core.binding;

import java.util.ArrayList;
import java.util.List;

import laf.component.core.binding.BindingExpressionExecutionLogManager.MethodInvocation;

class BindingExpressionExecutionLog {
	BindingGroup<?> involvedBindingGroup;
	List<MethodInvocation> modelPath = new ArrayList<>();
	List<MethodInvocation> componentPath = new ArrayList<>();
	BindingTransformer<?, ?> transformer;
	boolean transformInv;
}