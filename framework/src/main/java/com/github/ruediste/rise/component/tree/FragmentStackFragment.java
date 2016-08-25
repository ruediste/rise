package com.github.ruediste.rise.component.tree;

import java.util.ArrayDeque;
import java.util.Collections;

import com.github.ruediste.rendersnakeXT.canvas.HtmlConsumer;

public class FragmentStackFragment extends Component {

    ArrayDeque<Component> currentStack = new ArrayDeque<>();
    ArrayDeque<Component> newStack;

    boolean isTransient;

    @Override
    public Iterable<Component> getChildren() {
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

    private ArrayDeque<Component> getNewStack() {
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

    void push(Component fragment) {
        getNewStack().push(fragment);
    }

    void pop() {
        getNewStack().pop();
    }

}
