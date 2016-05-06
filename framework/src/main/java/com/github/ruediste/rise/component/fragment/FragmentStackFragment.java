package com.github.ruediste.rise.component.fragment;

import java.util.ArrayDeque;
import java.util.Collections;

import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;

public class FragmentStackFragment extends HtmlFragment {

    ArrayDeque<HtmlFragment> currentStack = new ArrayDeque<>();
    ArrayDeque<HtmlFragment> newStack;

    boolean isTransient;

    @Override
    public Iterable<HtmlFragment> getChildren() {
        if (isTransient) {
            if (currentStack.isEmpty())
                return Collections.emptyList();
            else
                return Collections.singleton(currentStack.peek());
        } else
            return currentStack;
    }

    @Override
    protected void produceHtml(HtmlConsumer consumer) {
        if (currentStack.isEmpty()) {
            currentStack.peek().render(consumer);
        }
    }

    @Override
    public void updateStructure(UpdateStructureArg arg) {
        if (newStack != null) {
            currentStack = newStack;
            newStack = null;
            arg.structureUpdated();
        }
    }

    private ArrayDeque<HtmlFragment> getNewStack() {
        if (newStack == null)
            newStack = new ArrayDeque<>(currentStack);
        return newStack;
    }

    /**
     * Make the stack transient, such that only the top of the stack is part of
     * the fragment hierarchy
     */
    public FragmentStackFragment setTransient() {
        isTransient = true;
        return this;
    }

    void push(HtmlFragment fragment) {
        getNewStack().push(fragment);
    }

    void pop() {
        getNewStack().pop();
    }

}
